package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.FeedUpdater;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.factoryinterfaces.FeedDAOInterface;

public class FeedDAO implements FeedDAOInterface {

    public Pair<List<DynamoFeedStatus>, Boolean> getFeed(String alias, Status lastStatus, int limit) {
        List<DynamoFeedStatus> dynamoStatusList = new ArrayList<>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table feedTable = dynamoDB.getTable("feed");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":f", alias);

        boolean hasMorePages = true;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("user_handle = :f")
                .withValueMap(valueMap).withMaxResultSize(limit);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        if (lastStatus != null) {
            querySpec.withExclusiveStartKey("user_handle", alias, "date", lastStatus.getDate());
        }
        items = feedTable.query(querySpec);
        try {
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                dynamoStatusList.add(new DynamoFeedStatus(
                        item.getString("post"),
                        item.getString("postedBy"),
                        item.getString("date"),
                        item.getList("urls"),
                        item.getList("mentions")
                ));
                // create Status Object add to list
            }
            if (items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() == null) {
                hasMorePages = false;
            }
        } catch (Exception e) {
            throw e;
        }
        // Create list of <post, datetime> pairs and then user that list to create statuses with the users it gets.

        return new Pair<>(dynamoStatusList, hasMorePages);
    }


    public void addStatus(Item status) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table feedTable = dynamoDB.getTable("feed");
        System.out.println("inside feed addstatus");
        try {
            PutItemOutcome outcome = feedTable.putItem(status);
            System.out.println("after outcome");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }


    public void addStatusBatch(FeedUpdater feedUpdater) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems("feed");
        List<String> followersAlias = feedUpdater.getFollowers();
        // Add each user into the TableWriteItems object
        for (String alias : followersAlias) {
            Item feedStatus = new Item().withPrimaryKey("user_handle", alias, "date", feedUpdater.getDatetime())
                    .with("postedBy", feedUpdater.getAlias())
                    .with("post", feedUpdater.getPost())
                    .with("mentions", feedUpdater.getMentions())
                    .with("urls", feedUpdater.getUrls());

            items.addItemToPut(feedStatus);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == followersAlias.size()) {
                loopBatchWrite(items);
                items = new TableWriteItems("feed");
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
        }
    }



    public static class DynamoFeedStatus {
        //new Status("post", "user", "datetime", "url string list", "mentions string list")
        String post;
        String userAlias;
        String dateTime;
        List<String> urls;
        List<String> mentions;

        public DynamoFeedStatus(String post, String userAlias, String dateTime, List<String> urls, List<String> mentions) {
            this.post = post;
            this.userAlias = userAlias;
            this.dateTime = dateTime;
            this.urls = urls;
            this.mentions = mentions;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }

        public String getUserAlias() {
            return userAlias;
        }

        public void setUserAlias(String userAlias) {
            this.userAlias = userAlias;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }

        public List<String> getMentions() {
            return mentions;
        }

        public void setMentions(List<String> mentions) {
            this.mentions = mentions;
        }
    }
}

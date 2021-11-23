package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.factoryinterfaces.StoryDAOInterface;

public class StoryDAO implements StoryDAOInterface {

    public Pair<List<DynamoStoryStatus>, Boolean> getStory(String alias, Status lastStatus, int limit) {
        System.out.println("start getStory()");
        List<DynamoStoryStatus> dynamoStatusList = new ArrayList<>();
        System.out.println("afterlist getStory()");
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        System.out.println("after client getStory()");
        DynamoDB dynamoDB = new DynamoDB(client);
        System.out.println("getTable getStory()");
        Table feedTable = dynamoDB.getTable("story");
        System.out.println("after getTable getStory()");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":f", alias);
        System.out.println("hasMorePages getStory()");
        boolean hasMorePages = true;

        System.out.println("queryspec getStory()");
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("user_handle = :f")
                .withValueMap(valueMap).withMaxResultSize(limit);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        if (lastStatus != null) {
            querySpec.withExclusiveStartKey("user_handle", alias, "date", lastStatus.getDate());
        }
        items = feedTable.query(querySpec);
        System.out.println("before try getStory()");
        try {
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                dynamoStatusList.add(new DynamoStoryStatus(
                        item.getString("post"),
                        item.getString("user_handle"),
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
            System.out.println("exception getStory()");
            throw e;
        }
        System.out.println("return getStory()");
        return new Pair<>(dynamoStatusList, hasMorePages);
    }

    public void addStatus(Item status) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table storyTable = dynamoDB.getTable("story");

        try {
            PutItemOutcome outcome = storyTable.putItem(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public static class DynamoStoryStatus {
        //new Status("post", "user", "datetime", "url string list", "mentions string list")
        String post;
        String userAlias;
        String dateTime;
        List<String> urls;
        List<String> mentions;

        public DynamoStoryStatus(String post, String userAlias, String dateTime, List<String> urls, List<String> mentions) {
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

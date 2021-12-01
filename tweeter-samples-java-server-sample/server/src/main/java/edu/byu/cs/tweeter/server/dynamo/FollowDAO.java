package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.factoryinterfaces.FollowDAOInterface;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements FollowDAOInterface {


    public Boolean isFollower(String followerHandle, String followeeHandle) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table followsTable = dynamoDB.getTable("follows");



//        Index index = followsTable.getIndex("follows_index");
//        HashMap<String, Object> valueMap = new HashMap<String, Object>();
//        valueMap.put(":f", followerHandle);
//        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("followee_handle = :f")
//                .withValueMap(valueMap).withMaxResultSize(limit);
//        querySpec.withExclusiveStartKey("followee_handle", currentUser, "follower_handle", lastFolloweeHandle);
//        items = index.query(querySpec);
//




        GetItemSpec getItemSpec = new GetItemSpec().withPrimaryKey("follower_handle", followerHandle, "followee_handle", followeeHandle);
        System.out.println("before outcome");
        Item outcome = followsTable.getItem(getItemSpec);
        System.out.println("after outcome");
        return outcome == null;
    }

    public void follow(String userAlias, String followee) {
        // Still need to test.
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("follower_handle",
                            followee,
                            "followee_handle",
                            userAlias));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            System.err.println("Unable to add item.");
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public void unfollow(String userAlias, String followee) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");

        try {
            System.out.println("Removing an item...");
            DeleteItemOutcome outcome = table.deleteItem("follower_handle",
                    followee, "followee_handle", userAlias);
            System.out.println("Delete succeeded:\n" + outcome.getDeleteItemResult());
        } catch (Exception e) {
            System.err.println("Unable to delete item.");
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public Pair<List<String>, Boolean> getFollowersAlias(String currentUserAlias, String lastFollowerAlias, int limit) {
        List<String> aliasList = new ArrayList<>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":f", currentUserAlias);

        boolean hasMorePages = true;

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :f")
                .withValueMap(valueMap).withMaxResultSize(limit);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        if (lastFollowerAlias != null) {
            querySpec.withExclusiveStartKey("follower_handle", currentUserAlias, "followee_handle", lastFollowerAlias);
        }
        items = table.query(querySpec);
        try {
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                aliasList.add(item.getString("followee_handle"));
            }
            if (items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() == null) {
                hasMorePages = false;
            }
        } catch (Exception e) {
            System.err.println("CS340 data");
            System.err.println(e.getMessage());
            throw e;
        }
        Pair<List<String>, Boolean> resultPair = new Pair<>(aliasList, hasMorePages);
        return resultPair;
    }

    public Pair<List<String>, Boolean> getFollowingAlias(String currentUserAlias, String lastFolloweeAlias, int limit) {
        List<String> aliasList = new ArrayList<>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");
        Index index = table.getIndex("follows_index");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":f", currentUserAlias);

        boolean hasMorePages = true;

        String lastFolloweeHandle = lastFolloweeAlias;
        String currentUser = currentUserAlias;


        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("followee_handle = :f")
                .withValueMap(valueMap).withMaxResultSize(limit);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        if (lastFolloweeHandle != null) {
            querySpec.withExclusiveStartKey("followee_handle", currentUser, "follower_handle", lastFolloweeHandle);
        }
        items = index.query(querySpec);
        try {
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                aliasList.add(item.getString("follower_handle"));
            }
            if (items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() == null) {
                hasMorePages = false;
            }
        } catch (Exception e) {
            System.err.println("CS340 data");
            System.err.println(e.getMessage());
        }
        Pair<List<String>, Boolean> resultPair = new Pair<>(aliasList, hasMorePages);
        return resultPair;
    }



    public void addFollowersBatch(List<String> users, String followTarget) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems("follows");

        // Add each user into the TableWriteItems object
        for (String alias : users) {
            Item item = new Item()
                    .withPrimaryKey("follower_handle", followTarget, "followee_handle", alias) ;
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("follows");
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
}

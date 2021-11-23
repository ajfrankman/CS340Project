package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
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

        GetItemSpec getItemSpec = new GetItemSpec().withPrimaryKey("follower_handle", followerHandle, "followee_handle", followeeHandle);

        try {
            followsTable.getItem(getItemSpec);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void follow(String userAlias, String followee) {
        // Still need to test.
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("follower_handle",
                            userAlias,
                            "followee_handle",
                            followee));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            System.err.println("Unable to add item.");
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public void unfollow(String userAlias, String followee) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");

        try {
            System.out.println("Removing an item...");
            DeleteItemOutcome outcome = table.deleteItem("follower_handle",
                    userAlias, "followee_handle", followee);
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

        String lastFollowerHandle = lastFollowerAlias;
        String currentUser = currentUserAlias;


        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :f")
                .withValueMap(valueMap).withMaxResultSize(limit);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        if (lastFollowerHandle != null) {
            querySpec.withExclusiveStartKey("follower_handle", currentUser, "followee_handle", lastFollowerHandle);
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
}

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
import edu.byu.cs.tweeter.server.factoryinterfaces.FollowDAOInterface;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements FollowDAOInterface {

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        return new IsFollowerResponse(true);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        return new GetFollowingCountResponse(9);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        return new GetFollowersCountResponse(8);
    }

    public FollowResponse follow(FollowRequest request) {
        // Still need to test.
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("follows");

//        final Map<String, Object> infoMap = new HashMap<String, Object>();
//        infoMap.put("plot", "Nothing happens at all.");
//        infoMap.put("rating", 0);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i != j) {
                    try {
                        System.out.println("Adding a new item...");
                        PutItemOutcome outcome = table
                                .putItem(new Item()
                                        .withPrimaryKey("follower_handle", "@" + request.getAuthToken().getUserAlias(), "followee_handle", request.getFollowee()));

                        System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

                    } catch (Exception e) {
                        System.err.println("Unable to add item.");
                        System.err.println(e.getMessage());
                    }
                }
            }
        }


        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return new UnfollowResponse();
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowersResponse getFollowers(FollowersRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;

        List<User> allFollowers = getDummyFollowers();
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followersIndex = getFollowersStartingIndex(request.getLastFollowerAlias(), allFollowers);

                for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followersIndex));
                }

                hasMorePages = followersIndex < allFollowers.size();
            }
        }

        return new FollowersResponse(responseFollowers, hasMorePages);
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowing(FollowingRequest request) {

//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
//        DynamoDB dynamoDB = new DynamoDB(client);
//        Table table = dynamoDB.getTable("follows");
//
//        HashMap<String, Object> valueMap = new HashMap<String, Object>();
//        valueMap.put(":f", request.getAuthToken().getUserAlias());
//
//        boolean notEmpty = true;
//
//        String lastFollowerHandle = null;
//        String lastFolloweeHandle = null;
//        while (notEmpty) {
//            QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :f")
//                    .withValueMap(valueMap).withMaxResultSize(1);
//
//            ItemCollection<QueryOutcome> items = null;
//            Iterator<Item> iterator = null;
//            Item item = null;
//
//            if (lastFolloweeHandle != null && lastFollowerHandle != null) {
//                querySpec.withExclusiveStartKey("follower_handle", lastFollowerHandle, "followee_handle", lastFolloweeHandle);
//            }
//            items = table.query(querySpec);
//            try {
//                System.out.println("CS340 data");
//
//
//                iterator = items.iterator();
//                while (iterator.hasNext()) {
//                    item = iterator.next();
//                    System.out.println(item.getString("follower_handle") + ": " + item.getString("followee_handle"));
//                }
//                if (items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() == null) {
//                    notEmpty = false;
//                } else {
//                    lastFollowerHandle = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey().get("follower_handle").getS();
//                    lastFolloweeHandle = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey().get("followee_handle").getS();
//                    System.out.println(lastFollowerHandle + lastFolloweeHandle);
//                }
//            } catch (Exception e) {
//                System.err.println("CS340 data");
//                System.err.println(e.getMessage());
//            }
//        }










                // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFollowerAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowers the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}

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
    @Override
    public StoriesResponse getStories(StoriesRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getLastStatus() != null;

        List<Status> allStories = getDummyStories();
        List<Status> responseStories = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if (allStories != null) {
                int followersIndex = getStoriesStartingIndex(request.getLastStatus(), allStories);

                for (int limitCounter = 0; followersIndex < allStories.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseStories.add(allStories.get(followersIndex));
                }

                hasMorePages = followersIndex < allStories.size();
            }
        }

        return new StoriesResponse(responseStories, hasMorePages);

    }

    public Pair<List<DynamoStoryStatus>, Boolean> getStory(String alias, Status lastStatus, int limit) {
        List<DynamoStoryStatus> dynamoStatusList = new ArrayList<>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table feedTable = dynamoDB.getTable("story");

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
                dynamoStatusList.add(new DynamoStoryStatus(
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

        Pair<List<DynamoStoryStatus>, Boolean> test = new Pair<>(dynamoStatusList, hasMorePages);
        return new Pair<>(dynamoStatusList, hasMorePages);
    }

    private int getStoriesStartingIndex(Status lastStory, List<Status> allStories) {

        int storyIndex = 0;

        if(lastStory != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStories.size(); i++) {
                if(lastStory.equals(allStories.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    storyIndex = i + 1;
                    break;
                }
            }
        }

        return storyIndex;
    }

    List<Status> getDummyStories() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return new FakeData();
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

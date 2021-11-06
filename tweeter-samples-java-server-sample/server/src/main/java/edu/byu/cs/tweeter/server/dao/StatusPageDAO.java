package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

public class StatusPageDAO {

    public FeedResponse getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getLastStatus() != null;

        List<Status> wholeFeed = getDummyStories();
        List<Status> responseFeed = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if (wholeFeed != null) {
                int followersIndex = getFeedStartingIndex(request.getLastStatus(), wholeFeed);

                for (int limitCounter = 0; followersIndex < wholeFeed.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFeed.add(wholeFeed.get(followersIndex));
                }

                hasMorePages = followersIndex < wholeFeed.size();
            }
        }

        return new FeedResponse(responseFeed, hasMorePages);
    }

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

    private int getFeedStartingIndex(Status lastStory, List<Status> wholeFeed) {

        int storyIndex = 0;

        if(lastStory != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < wholeFeed.size(); i++) {
                if(lastStory.equals(wholeFeed.get(i))) {
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
}

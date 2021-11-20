package edu.byu.cs.tweeter.server.factoryinterfaces;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;

public interface StatusPageDAOInterface {
    public FeedResponse getFeed(FeedRequest request);
    public StoriesResponse getStories(StoriesRequest request);

}

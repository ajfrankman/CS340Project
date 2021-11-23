package edu.byu.cs.tweeter.server.factoryinterfaces;

import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;

public interface StoryDAOInterface {
    public StoriesResponse getStories(StoriesRequest request);
}

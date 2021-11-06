package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.server.dao.StatusPageDAO;

public class StatusPageService {
    public StoriesResponse getStories(StoriesRequest request) {
        return getStatusPageDAO().getStories(request);
    }

    public FeedResponse getFeed(FeedRequest request) {
        return getStatusPageDAO().getFeed(request);
    }

    StatusPageDAO getStatusPageDAO() {
        return new StatusPageDAO();
    }
}

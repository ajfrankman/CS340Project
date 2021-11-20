package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.dynamo.StatusPageDAO;
import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

public class StatusPageService {

    DAOFactory currentFactory;

    public StatusPageService(DAOFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    public StoriesResponse getStories(StoriesRequest request) {
        if (request.getLimit() < 1) throw new RuntimeException("Invalid request object");
        return currentFactory.getStatusPageDAO().getStories(request);
    }

    public FeedResponse getFeed(FeedRequest request) {
        return currentFactory.getStatusPageDAO().getFeed(request);
    }
}

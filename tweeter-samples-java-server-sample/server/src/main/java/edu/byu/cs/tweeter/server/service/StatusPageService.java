package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dynamo.FeedDAO;
import edu.byu.cs.tweeter.server.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

public class StatusPageService {

    DAOFactory currentFactory;

    public StatusPageService(DAOFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    public StoriesResponse getStories(StoriesRequest request) {
        if (request.getLimit() < 1) throw new RuntimeException("Invalid request object");
        // given a user and lastStatus get next ten or less statuses from feed table.
        Pair<List<StoryDAO.DynamoStoryStatus>, Boolean> responsePair = currentFactory.getStoryDAO().getStory(request.getLastStatus().getUser().getAlias(), request.getLastStatus(), request.getLimit());
        List<StoryDAO.DynamoStoryStatus> dynamoStoryStatus = responsePair.getFirst();
        Boolean hasMorePages = responsePair.getSecond();
        // Convert dynamoStatusResponse objects into Statuses

        List<Status> statuses = new ArrayList<>();
        for (int i = 0; i < dynamoStoryStatus.size(); i++) {
            User user = currentFactory.getUserDAO().getUser(dynamoStoryStatus.get(i).getUserAlias());
            Status status = new Status(
                    dynamoStoryStatus.get(i).getPost(),
                    user,
                    dynamoStoryStatus.get(i).getDateTime(),
                    dynamoStoryStatus.get(i).getUrls(),
                    dynamoStoryStatus.get(i).getMentions()
            );
            statuses.add(status);
        }

        return new StoriesResponse(statuses, hasMorePages);
    }

    public FeedResponse getFeed(FeedRequest request) {
        if (request.getLimit() < 1) throw new RuntimeException("Invalid request object");

        // given a user and lastStatus get next ten or less statuses from feed table.
        Pair<List<FeedDAO.DynamoFeedStatus>, Boolean> responsePair = currentFactory.getFeedDAO().getFeed(request.getLastStatus().getUser().getAlias(), request.getLastStatus(), request.getLimit());
        List<FeedDAO.DynamoFeedStatus> dynamoFeedStatus = responsePair.getFirst();
        Boolean hasMorePages = responsePair.getSecond();
        // Convert dynamoStatusResponse objects into Statuses
        List<Status> statuses = new ArrayList<>();
        for (int i = 0; i < dynamoFeedStatus.size(); i++) {
            User user = currentFactory.getUserDAO().getUser(dynamoFeedStatus.get(i).getUserAlias());
            Status status = new Status(
                    dynamoFeedStatus.get(i).getPost(),
                    user,
                    dynamoFeedStatus.get(i).getDateTime(),
                    dynamoFeedStatus.get(i).getUrls(),
                    dynamoFeedStatus.get(i).getMentions()
            );
            statuses.add(status);
        }

        return new FeedResponse(statuses, hasMorePages);
    }
}

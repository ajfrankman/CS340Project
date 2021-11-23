package edu.byu.cs.tweeter.server.factoryinterfaces;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dynamo.StoryDAO;

public interface StoryDAOInterface {
    public Pair<List<StoryDAO.DynamoStoryStatus>, Boolean> getStory(String alias, Status lastStatus, int limit);
    public void addStatus(Item status);
}

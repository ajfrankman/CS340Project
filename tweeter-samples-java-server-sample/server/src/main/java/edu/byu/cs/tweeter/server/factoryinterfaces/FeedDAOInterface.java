package edu.byu.cs.tweeter.server.factoryinterfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dynamo.FeedDAO;

public interface FeedDAOInterface {
    public Pair<List<FeedDAO.DynamoFeedStatus>, Boolean> getFeed(String alias, Status lastStatus, int limit);

}

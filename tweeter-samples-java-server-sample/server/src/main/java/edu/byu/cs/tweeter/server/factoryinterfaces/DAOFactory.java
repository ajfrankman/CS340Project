package edu.byu.cs.tweeter.server.factoryinterfaces;

import edu.byu.cs.tweeter.server.dynamo.AuthDAO;
import edu.byu.cs.tweeter.server.dynamo.FeedDAO;
import edu.byu.cs.tweeter.server.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dynamo.StoryDAO;
import edu.byu.cs.tweeter.server.dynamo.UserDAO;

public interface DAOFactory {
    UserDAO getUserDAO();
    AuthDAO getAuthDAO();
    FollowDAO getFollowDAO();
    FeedDAO getFeedDAO();
    StoryDAO getStoryDAO();
}

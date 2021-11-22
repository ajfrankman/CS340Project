package edu.byu.cs.tweeter.server.factoryinterfaces;

import edu.byu.cs.tweeter.server.dynamo.AuthDAO;
import edu.byu.cs.tweeter.server.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dynamo.StatusPageDAO;
import edu.byu.cs.tweeter.server.dynamo.UserDAO;

public interface DAOFactory {
    UserDAO getUserDAO();
    AuthDAO getAuthDAO();
    StatusPageDAO getStatusPageDAO();
    FollowDAO getFollowDAO();
}

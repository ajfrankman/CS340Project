package edu.byu.cs.tweeter.server.dynamo;

import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

public class DynamoDBFactory implements DAOFactory {

    private static DynamoDBFactory INSTANCE;
    private DynamoDBFactory() {
    }

    public static DynamoDBFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DynamoDBFactory();
        }

        return INSTANCE;
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAO();
    }

    @Override
    public AuthDAO getAuthDAO() {
        return new AuthDAO();
    }

    @Override
    public StatusPageDAO getStatusPageDAO() {
        return new StatusPageDAO();
    }

    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAO();
    }
}

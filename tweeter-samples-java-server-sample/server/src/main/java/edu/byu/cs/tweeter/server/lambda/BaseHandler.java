package edu.byu.cs.tweeter.server.lambda;

import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

public class BaseHandler {
    DAOFactory currentFactory = DynamoDBFactory.getInstance();
}

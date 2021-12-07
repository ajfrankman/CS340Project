package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dynamo.UserDAO;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetUserHandler extends BaseHandler implements RequestHandler<UserRequest, UserResponse> {

    @Override
    public UserResponse handleRequest(UserRequest userRequest, Context context) {
        UserService userService = new UserService(currentFactory);
        return userService.getUser(userRequest);
    }


//    public static void main(String[] args) {
//        AuthToken authToken = new AuthToken("536eeb9d-82f3-43cf-bf12-9bd3b7b68f00", "2021-12-06T00:14:41.671907", "@guy999");
//        UserRequest userRequest = new UserRequest(authToken, "@guy999");
//        DynamoDBFactory daoFactory = DynamoDBFactory.getInstance();
//        UserService userService = new UserService(daoFactory);
//        UserResponse userResponse = userService.getUser(userRequest);
//        System.out.println("hello");
//    }


}

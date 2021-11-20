package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.dynamo.UserDAO;
import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

public class UserService {

    DAOFactory currentFactory;

    public UserService(DAOFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    public LoginResponse login(LoginRequest request) {
        return currentFactory.getUserDAO().login(request);
    }

    public RegisterResponse register(RegisterRequest request) {
        return currentFactory.getUserDAO().register(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return currentFactory.getUserDAO().logout(request);
    }

    public UserResponse getUser(UserRequest request) {
        return currentFactory.getUserDAO().getUser(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        return currentFactory.getUserDAO().postStatus(postStatusRequest);
    }
}

package edu.byu.cs.tweeter.server.service;

import java.net.URL;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
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
        if (request.getPassword() == null || request.getUsername() == null) {
            throw new RuntimeException("Invalid request object");
        }
        // getUser()
        User user = currentFactory.getUserDAO().getUser(request.getUsername());
        // getAuthToken()
        AuthToken authToken = currentFactory.getAuthDAO().generateAuthToken(request.getUsername());
        // Create Response()
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getAlias() == null || request.getPassword() == null || request.getFirstName() == null || request.getLastName() == null || request.getImageBytesBase64() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // add User()
        currentFactory.getUserDAO().addUser(request);
        // add image()
        URL url = currentFactory.getUserDAO().addUserImage(request);
        // getUser() **maybe just create here without grabbing?**
        User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getAlias(), url.toString());
        // generateAuthToken()
        AuthToken authToken = currentFactory.getAuthDAO().generateAuthToken(request.getAlias());
        // Create Response
        return new RegisterResponse(registeredUser, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return currentFactory.getUserDAO().logout(request);
    }

    public UserResponse getUser(UserRequest request) {
        // getUser()
        User user = currentFactory.getUserDAO().getUser(request.getAlias());
        // Create Response()
        return new UserResponse(user);
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        return currentFactory.getUserDAO().postStatus(postStatusRequest);
    }
}

package edu.byu.cs.tweeter.server.factoryinterfaces;

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

public interface UserDAOInterface {
    // public LoginResponse login(LoginRequest request);
    // public RegisterResponse register(RegisterRequest request);
    public LogoutResponse logout(LogoutRequest request);
    // public UserResponse getUser(UserRequest request);
    public User getUser(String userAlias);
    // public PostStatusResponse postStatus(PostStatusRequest postStatusRequest);
}

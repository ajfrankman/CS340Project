package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

public class UserService {

    public LoginResponse login(LoginRequest request) {

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {

        // TODO: Generates dummy data. Replace with a real implementation.
        User registeredUser = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(registeredUser, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        // TODO: Replace with real Implementation
        return new LogoutResponse();
    }
    
    public UserResponse getUser(UserRequest request) {
        User user = getDummyUser();
        return new UserResponse(user);
    }

    public FollowResponse follow(FollowRequest request) {
        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return new UnfollowResponse();
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        // TODO: Handle the status and stuff
        return new PostStatusResponse();
    }
    /**
     * Returns the dummy user to be returned by the login/register operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

//    Status getDummyStatus() {
//        return getFakeData().getPageOfStatus(null, 1).getFirst().get(0);
//    }

    /**
     * Returns the dummy auth token to be returned by the login/register operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}

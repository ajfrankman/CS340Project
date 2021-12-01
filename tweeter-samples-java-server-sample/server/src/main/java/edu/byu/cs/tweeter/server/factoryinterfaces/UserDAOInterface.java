package edu.byu.cs.tweeter.server.factoryinterfaces;

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

public interface UserDAOInterface {
    public void addUser(String alias, String firstName, String lastName, String password, String imageURL);
    public User getUser(String userAlias);
    public URL addUserImage(String alias, String image);
    public void updateFollowingCount(String userAlias, int update);
    public void updateFollowersCount(String userAlias, int update);
    public int getUserFollowingCount(String userAlias);
    public int getUserFollowersCount(String userAlias);
}

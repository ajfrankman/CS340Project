package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest {
    AuthToken authToken;
    User Follower;
    User Followee;

    IsFollowerRequest() {}


    public IsFollowerRequest(AuthToken authToken, User follower, User followee) {
        this.authToken = authToken;
        Follower = follower;
        Followee = followee;
    }


    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollower() {
        return Follower;
    }

    public void setFollower(User follower) {
        Follower = follower;
    }

    public User getFollowee() {
        return Followee;
    }

    public void setFollowee(User followee) {
        Followee = followee;
    }
}

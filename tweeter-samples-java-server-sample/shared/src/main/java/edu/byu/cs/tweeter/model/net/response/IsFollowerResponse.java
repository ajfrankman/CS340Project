package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;

public class IsFollowerResponse extends Response {
    boolean isFollower;

    public IsFollowerResponse() {
        super(false, "default");
    }

    public IsFollowerResponse(String message) { super(false, message);}

    public IsFollowerResponse(boolean isFollower) {
        super(true, null);
        this.isFollower = isFollower;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }
}

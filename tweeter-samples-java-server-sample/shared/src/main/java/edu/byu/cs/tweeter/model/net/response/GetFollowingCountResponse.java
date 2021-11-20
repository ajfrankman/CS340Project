package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response {
    int count;

    public GetFollowingCountResponse () {
        super(false, "default");
    }

    public GetFollowingCountResponse(String message) {
        super(false, message);
    }

    public GetFollowingCountResponse(int count) {
        super(true, null);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

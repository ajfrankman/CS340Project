package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.Status;

public class FeedRequest {

    private Status lastStatus;
    private int limit;

    private FeedRequest() {}

    public FeedRequest(Status lastStatus, int limit) {
        this.lastStatus = lastStatus;
        this.limit = limit;
    }

    public Status getLastStatus() {
        return lastStatus;
    }

    public int getLimit() {
        return limit;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

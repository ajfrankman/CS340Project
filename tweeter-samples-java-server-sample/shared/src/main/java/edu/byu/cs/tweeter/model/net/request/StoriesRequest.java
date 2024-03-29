package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoriesRequest {

    public Status lastStatus;
    public int limit;
    public String userAlias;
    public AuthToken authToken;
    //TODO Authtoken

    private StoriesRequest() {}

    public StoriesRequest(Status lastStatus, int limit, String userAlias, AuthToken authToken) {
        this.lastStatus = lastStatus;
        this.limit = limit;
        this.userAlias = userAlias;
        this.authToken = authToken;
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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}

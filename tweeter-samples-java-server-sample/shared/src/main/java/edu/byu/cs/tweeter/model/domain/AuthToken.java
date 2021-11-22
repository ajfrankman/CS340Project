package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {

    /**
     * Value of the auth token.
     */
    public String token;
    public String userAlias;
    /**
     * String representation of date/time at which the auth token was created.
     */
    public String datetime;

    public AuthToken() {
    }

    public AuthToken(String token) {
        this.token = token;
    }

    public AuthToken(String token, String datetime, String userAlias) {
        this.token = token;
        this.datetime = datetime;
        this.userAlias = userAlias;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDatetime() {
        return datetime;
    }

    public boolean equals(AuthToken other) {
        return token.equals(other.token);
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterResponse extends Response{
    User registeredUser;
    AuthToken authToken;

    public RegisterResponse(String message) { super(false, message); }

    public RegisterResponse(User registeredUser, AuthToken authToken) {
        super(true, null);
        this.registeredUser = registeredUser;
        this.authToken = authToken;
    }


    public User getRegisteredUser() {
        return registeredUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class UserResponse extends Response {

    public User user;

    public UserResponse() {
        super(false, "default");
    }

    //Unsuccessful
    public UserResponse(String message) {
        super(false, message);
    }

    //Successful
    public UserResponse(User user) {
        super(true, null);
        this.user = user;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

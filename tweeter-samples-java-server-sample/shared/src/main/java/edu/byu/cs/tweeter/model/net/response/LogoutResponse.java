package edu.byu.cs.tweeter.model.net.response;

public class LogoutResponse extends Response {

    //Failure
    public LogoutResponse (String message) { super(false, message); }

    //Success
    public LogoutResponse() {
        super(true, null);
    }
}

package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthorizedTask {
    private static final String LOG_TAG = "IsFollowerTask";

    public static final String SUCCESS_KEY = "success";
    public static final String IS_FOLLOWER_KEY = "is-follower";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    private static final String URL_PATH = "/isfollower";
    ServerFacade serverFacade;


    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;


    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.follower = follower;
        this.followee = followee;
    }

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, new Random().nextInt() > 0);
    }

    @Override
    protected void runTask() throws IOException {
        isFollower();
    }

    public boolean isFollower() {
        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(this.authToken, this.follower, this.followee);
        IsFollowerResponse isFollowerResponse = null;
        try {
            isFollowerResponse = getServerFacade().isFollower(isFollowerRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
        //TODO: This returns a bool one whether or not they are a follower. Not the functions success like others. Don't know what is right. I think this. The others might should just return void.
        return isFollowerResponse.isFollower();
    }

}

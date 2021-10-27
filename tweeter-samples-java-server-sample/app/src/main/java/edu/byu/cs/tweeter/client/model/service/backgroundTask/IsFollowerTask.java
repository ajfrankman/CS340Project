package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthorizedTask {
    private static final String LOG_TAG = "IsFollowerTask";

    public static final String SUCCESS_KEY = "success";
    public static final String IS_FOLLOWER_KEY = "is-follower";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

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

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, new Random().nextInt() > 0);
    }

    @Override
    protected void runTask() throws IOException {
    }
}

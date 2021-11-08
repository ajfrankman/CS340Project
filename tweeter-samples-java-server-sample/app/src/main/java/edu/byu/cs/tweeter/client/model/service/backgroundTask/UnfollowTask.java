package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthorizedTask {
    private static final String LOG_TAG = "UnfollowTask";

    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    private static final String URL_PATH = "/unfollow";
    /**
     * The user that is being followed.
     */
    private User followee;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.followee = followee;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {

    }

    @Override
    protected void runTask() throws IOException {
        unfollow();
    }

    private boolean unfollow() {
        UnfollowRequest unfollowRequest = new UnfollowRequest(this.authToken, this.followee);
        try {
            UnfollowResponse unfollowResponse = getServerFacade().follow(unfollowRequest, URL_PATH);
            return unfollowResponse.isSuccess();
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}

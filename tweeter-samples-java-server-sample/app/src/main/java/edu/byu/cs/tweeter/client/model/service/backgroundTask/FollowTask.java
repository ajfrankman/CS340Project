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
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthorizedTask {
    private static final String LOG_TAG = "FollowTask";

    private static final String URL_PATH = "/follow";
    ServerFacade serverFacade;

    /**
     * The user that is being followed.
     */
    private User followee;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
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

    }

    @Override
    protected void runTask() throws IOException {
        follow();
    }

    private boolean follow() {
        FollowRequest followRequest = new FollowRequest(this.authToken, this.followee);
        try {
            FollowResponse followResponse = getServerFacade().follow(followRequest, URL_PATH);
            return followResponse.isSuccess();
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}

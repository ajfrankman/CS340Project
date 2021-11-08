package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthorizedTask {
    private static final String LOG_TAG = "PostStatusTask";

    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";
    private static final String URL_PATH = "/poststatus";
    ServerFacade serverFacade;


    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private Status status;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(messageHandler, authToken);
        this.status = status;
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
        postStatus();
    }

    public boolean postStatus() {
        PostStatusRequest postStatusRequest = new PostStatusRequest(this.authToken, this.status);
        try {
            PostStatusResponse postStatusResponse = getServerFacade().postStatus(postStatusRequest, URL_PATH);
            return postStatusResponse.isSuccess();
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}

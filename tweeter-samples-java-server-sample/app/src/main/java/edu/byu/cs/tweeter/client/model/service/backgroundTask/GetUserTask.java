package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthorizedTask {

    private static final String LOG_TAG = "GetUserTask";
    public static final String USER_KEY = "user";
    private static final String URL_PATH = "/getuser";


    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;

    ServerFacade serverFacade;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler, authToken);
        this.alias = alias;
    }


    private User getUser() {
        UserRequest userRequest = new UserRequest(this.authToken, this.alias);
        UserResponse userResponse = null;
        try {
            userResponse = getServerFacade().getUser(userRequest, URL_PATH);
            BackgroundTaskUtils.loadImage(userResponse.getUser());
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            return null;
        }
        return userResponse.getUser();
    }

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {

        msgBundle.putSerializable(USER_KEY, getUser());
    }

    @Override
    protected void runTask() {
        // Only needed becuase it will have actual code when we aren't using dummy data.
    }
}

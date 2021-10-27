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
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends BackgroundTask {

    private static final String LOG_TAG = "LoginTask";


    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    private User loggedInUser;
    private AuthToken authToken;

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, loggedInUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);

    }

    @Override
    protected void runTask() throws IOException {
        Pair<User, AuthToken> loginResult = doLogin();
        loggedInUser = loginResult.getFirst();
        authToken = loginResult.getSecond();
        BackgroundTaskUtils.loadImage(loggedInUser);
    }

    private Pair<User, AuthToken> doLogin() {
        LoginRequest loginRequest = new LoginRequest(this.username, this.password);
        LoginResponse loginResponse;
        try {
            loginResponse = serverFacade.login(loginRequest, "Need a URL");
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
        loggedInUser = getFakeData().getFirstUser();
        authToken = getFakeData().getAuthToken();
        return new Pair<>(loggedInUser, authToken);
    }
}

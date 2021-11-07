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
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends BackgroundTask {
    private static final String LOG_TAG = "RegisterTask";

    private static final String URL_PATH = "/register";
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    ServerFacade serverFacade;
    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;
    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    User registeredUser;
    AuthToken authToken;

    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(messageHandler);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.image = image;
    }

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, registeredUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }

    @Override
    protected void runTask() throws IOException {
        Pair<User, AuthToken> registerResult = doRegister();

        registeredUser = registerResult.getFirst();
        authToken = registerResult.getSecond();

        BackgroundTaskUtils.loadImage(registeredUser);
    }

    private Pair<User, AuthToken> doRegister() {

        RegisterRequest registerRequest = new RegisterRequest(this.username, this.password, this.username, this.password, this.image);
        RegisterResponse registerResponse = null;
        try {
            registerResponse = getServerFacade().register(registerRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        registeredUser = registerResponse.getRegisteredUser();
        authToken = registerResponse.getAuthToken();
        return new Pair<>(registeredUser, authToken);
    }

}

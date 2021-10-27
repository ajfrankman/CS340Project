package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.service.LoginService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginHandler extends BackgroundTaskHandler<LoginService.LoginObserver> {

    public LoginHandler(LoginService.LoginObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(LoginService.LoginObserver observer, Bundle bundle) {
        User loggedInUser = (User) bundle.getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) bundle.getSerializable(LoginTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.logoutSucceeded(authToken, loggedInUser);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Login Failed";
    }
}

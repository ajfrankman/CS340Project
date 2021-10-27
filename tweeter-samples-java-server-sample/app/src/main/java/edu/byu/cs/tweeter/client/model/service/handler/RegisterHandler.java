package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.service.RegisterService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends BackgroundTaskHandler<RegisterService.RegisterObserver> {

    public RegisterHandler(RegisterService.RegisterObserver observer) {
        super(observer);
    }


    @Override
    protected void handleSuccessMessage(RegisterService.RegisterObserver observer, Bundle bundle) {
        User registeredUser = (User) bundle.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) bundle.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        observer.registerSuccess(authToken, registeredUser);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Registration Failed";
    }
}

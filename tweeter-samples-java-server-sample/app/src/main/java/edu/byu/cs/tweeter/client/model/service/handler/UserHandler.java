package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

public class UserHandler extends BackgroundTaskHandler<UserService.UserObserver> {

    public UserHandler(UserService.UserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.UserObserver observer, Bundle bundle) {
        User user = (User) bundle.getSerializable(GetUserTask.USER_KEY);
        observer.getUserSucceeded(user);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Get User failed";
    }
}

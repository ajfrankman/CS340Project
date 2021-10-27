package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.service.LogoutService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;

// LogoutHandler
public class LogoutHandler extends BackgroundTaskHandler<LogoutService.LogoutObserver> {

    public LogoutHandler(LogoutService.LogoutObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(LogoutService.LogoutObserver observer, Bundle bundle) {
        observer.logoutSucceeded();
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Logout failed";
    }


}

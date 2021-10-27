package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.service.FollowService;

public class FollowHandler extends BackgroundTaskHandler<FollowService.FollowObserver> {

    public FollowHandler(FollowService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.FollowObserver observer, Bundle bundle) {
        observer.onFollowSuccess();
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Failed to follow";
    }
}

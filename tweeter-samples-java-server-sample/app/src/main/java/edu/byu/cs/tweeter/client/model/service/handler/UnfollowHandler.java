package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.service.UnfollowService;

//Handler gets result of executor
public class UnfollowHandler extends BackgroundTaskHandler<UnfollowService.UnfollowObserver> {

    public UnfollowHandler(UnfollowService.UnfollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UnfollowService.UnfollowObserver observer, Bundle bundle) {
        observer.UnfollowSucceeded();

    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Failed to unfollow";
    }


}

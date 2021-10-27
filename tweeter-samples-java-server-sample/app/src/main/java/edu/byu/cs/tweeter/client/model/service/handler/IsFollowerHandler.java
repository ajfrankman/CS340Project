package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.service.IsFollowerService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;

public class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerService.IsFollowerObserver> {

    public IsFollowerHandler(IsFollowerService.IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IsFollowerService.IsFollowerObserver observer, Bundle bundle) {
        observer.isFollowerSuccess(bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY));
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Isfollower failed";
    }


}

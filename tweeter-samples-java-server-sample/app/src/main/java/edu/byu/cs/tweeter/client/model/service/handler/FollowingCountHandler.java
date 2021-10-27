package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.service.FollowingCountService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;

public class FollowingCountHandler extends BackgroundTaskHandler<FollowingCountService.GetFollowingCountObserver> {

    public FollowingCountHandler(FollowingCountService.GetFollowingCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowingCountService.GetFollowingCountObserver observer, Bundle bundle) {
        int count = bundle.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.getFollowingCountSucceeded(count);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Failed to get following count";
    }

}

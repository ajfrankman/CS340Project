package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.service.FollowersCountService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;

// GetFollowersCountHandler
public class FollowersCountHandler extends BackgroundTaskHandler<FollowersCountService.GetFollowersCountObserver> {

    public FollowersCountHandler(FollowersCountService.GetFollowersCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowersCountService.GetFollowersCountObserver observer, Bundle bundle) {
        int count = bundle.getInt(GetFollowersCountTask.COUNT_KEY);
        observer.getFollowersCountSucceeded(count);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Failed to get followers count";
    }
}

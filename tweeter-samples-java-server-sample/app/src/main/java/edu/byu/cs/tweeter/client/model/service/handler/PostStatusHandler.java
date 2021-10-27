package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;

// PostStatusHandler
public class PostStatusHandler extends BackgroundTaskHandler<PostStatusService.PostStatusObserver> {

    public PostStatusHandler(PostStatusService.PostStatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PostStatusService.PostStatusObserver observer, Bundle bundle) {
        observer.postStatusSucceeded("Successfully Posted");
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Failed to post status";
    }


}

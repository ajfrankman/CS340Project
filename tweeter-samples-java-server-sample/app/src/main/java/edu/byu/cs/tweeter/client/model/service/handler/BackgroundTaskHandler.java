package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.service.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;

public abstract class BackgroundTaskHandler<T extends ServiceObserver> extends Handler {

    private T observer;

    public BackgroundTaskHandler(T observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
        if (success) {
            handleSuccessMessage(observer, msg.getData());
        } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
            observer.handleFailure(getFailedMessagePrefix() + ": " + message);
        } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
            observer.handleFailure(getFailedMessagePrefix() + " because of exception: " + ex.getMessage());
        }

    }

    protected abstract void handleSuccessMessage(T observer, Bundle bundle);
    protected abstract String getFailedMessagePrefix();
}

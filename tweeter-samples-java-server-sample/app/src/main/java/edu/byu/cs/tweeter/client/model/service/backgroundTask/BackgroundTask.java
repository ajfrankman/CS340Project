package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.FakeData;

public abstract class BackgroundTask implements Runnable {
    
    private static final String LOG_TAG = "BackgroundTask";
    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";
    //ServerFacade serverFacade = new ServerFacade();


    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    public BackgroundTask(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }


    @Override
    public void run() {
        try {
            runTask();
            sendSuccessMessage();

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    protected FakeData getFakeData() {
        return new FakeData();
    }

    protected abstract void loadMessageBundle(Bundle msgBundle);
    protected abstract void runTask() throws IOException;

    private void sendSuccessMessage() {
        Bundle msgBundle = createBundle(true);
        loadMessageBundle(msgBundle);
        sendMessage(msgBundle);
    }

    private void sendFailedMessage(String message) {
        Bundle msgBundle = createBundle(false);
        msgBundle.putString(MESSAGE_KEY, message);
        sendMessage(msgBundle);
    }

    private void sendExceptionMessage(Exception exception) {
        Bundle msgBundle = createBundle(false);
        msgBundle.putSerializable(EXCEPTION_KEY, exception);
        sendMessage(msgBundle);
    }


    @NonNull
    private Bundle createBundle(boolean value) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, value);
        return msgBundle;
    }

    private void sendMessage(Bundle msgBundle) {
        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }
}

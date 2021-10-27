package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.handler.LogoutHandler;

public class LogoutService extends SuperService{

    public void logout(LogoutObserver observer) {
        LogoutTask task = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer));
        execute(task);

    }

    public interface LogoutObserver extends ServiceObserver{
        void logoutSucceeded();

    }

}

package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.handler.UnfollowHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowService extends SuperService {

    public void Unfollow(UnfollowObserver observer, AuthToken authToken, User targetUser) {

        UnfollowTask task = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new UnfollowHandler(observer));
        execute(task);
    }
    public interface UnfollowObserver extends ServiceObserver {
        void UnfollowSucceeded();
    }


}

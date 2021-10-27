package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerService extends SuperService {

    public void IsFollower(IsFollowerObserver observer, User selectedUser) {
        IsFollowerTask task = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        execute(task);
    }

    public interface IsFollowerObserver extends ServiceObserver{
        public void isFollowerSuccess(boolean isFollower);
    }

}

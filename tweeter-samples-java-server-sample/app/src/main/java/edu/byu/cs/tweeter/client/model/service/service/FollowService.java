package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.handler.FollowHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends SuperService {

    public void Follow(FollowService.FollowObserver observer, AuthToken authToken, User targetUser) {

        FollowTask task = new FollowTask(authToken,
                targetUser, new FollowHandler(observer));
        execute(task);
    }

    public interface FollowObserver extends ServiceObserver {
        void onFollowSuccess();
    }

}

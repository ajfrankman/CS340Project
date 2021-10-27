package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.handler.FollowersCountHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersCountService extends SuperService {

    public void getFollowersCount(FollowersCountService.GetFollowersCountObserver observer, AuthToken authToken, User selectedUser) {
        GetFollowersCountTask task = new GetFollowersCountTask(authToken,
                selectedUser, new FollowersCountHandler(observer));
        execute(task);
    }

    public interface GetFollowersCountObserver extends ServiceObserver {
        void getFollowersCountSucceeded(int count);
    }

}
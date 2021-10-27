package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.handler.FollowingCountHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingCountService extends SuperService {

    public void getFollowingCount(FollowingCountService.GetFollowingCountObserver observer, AuthToken authToken, User selectedUser) {
        GetFollowingCountTask task = new GetFollowingCountTask(authToken,
                selectedUser, new FollowingCountHandler(observer));
        execute(task);
    }

    public interface GetFollowingCountObserver extends ServiceObserver{
        void getFollowingCountSucceeded(int count);
    }

}
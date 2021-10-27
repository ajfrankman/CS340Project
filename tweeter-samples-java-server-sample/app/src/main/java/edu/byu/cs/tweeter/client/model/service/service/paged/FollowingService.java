package edu.byu.cs.tweeter.client.model.service.service.paged;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.handler.FollowingHandler;
import edu.byu.cs.tweeter.client.model.service.service.SuperService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingService extends SuperService {
    public void getFollowing(AuthToken authToken, User targetUser, int limit,
                             User lastFollowee, PagesObserver observer) {
        GetFollowingTask task = new GetFollowingTask(authToken,
                targetUser, limit, lastFollowee, new FollowingHandler(observer));
        execute(task);
    }
}
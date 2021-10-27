package edu.byu.cs.tweeter.client.model.service.service.paged;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.handler.FollowersHandler;
import edu.byu.cs.tweeter.client.model.service.service.SuperService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerService extends SuperService {

    public void getFollowers(AuthToken authToken, User targetUser, int limit,
                             User lastFollower, PagesObserver observer) {

        GetFollowersTask task = new GetFollowersTask(authToken,
                targetUser, limit, lastFollower, new FollowersHandler(observer));
        execute(task);
    }
}
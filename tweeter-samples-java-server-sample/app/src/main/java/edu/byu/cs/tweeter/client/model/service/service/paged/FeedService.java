package edu.byu.cs.tweeter.client.model.service.service.paged;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.handler.FeedHandler;
import edu.byu.cs.tweeter.client.model.service.service.SuperService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService extends SuperService {

    public void getFeed(User targetUser, int limit,
                        Status lastStatus, PagesObserver observer) {

        GetFeedTask task = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, limit, lastStatus, new FeedHandler(observer));
        execute(task);
    }
}

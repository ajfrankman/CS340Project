package edu.byu.cs.tweeter.client.model.service.service.paged;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.handler.StoryHandler;
import edu.byu.cs.tweeter.client.model.service.service.SuperService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService extends SuperService {

    public void getStory(User targetUser, int limit,
                         Status lastStatus, PagesObserver observer) {
        GetStoryTask task = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, limit, lastStatus, new StoryHandler(observer));
        execute(task);
    }

}

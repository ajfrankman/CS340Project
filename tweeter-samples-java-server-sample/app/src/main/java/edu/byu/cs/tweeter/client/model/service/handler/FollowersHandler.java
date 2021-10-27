package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.service.paged.PagesObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersHandler extends BackgroundTaskHandler<PagesObserver> {

    public FollowersHandler(PagesObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagesObserver observer, Bundle bundle) {
        List<User> followers = (List<User>) bundle.getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = bundle.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        observer.getItemsSucceeded(followers, hasMorePages);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Get Followers Failed";
    }
}

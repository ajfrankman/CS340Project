package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.service.paged.PagesObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingHandler extends BackgroundTaskHandler<PagesObserver> {

    public FollowingHandler(PagesObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagesObserver observer, Bundle bundle) {
        List<User> followees = (List<User>) bundle.getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.getItemsSucceeded(followees, hasMorePages);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Get Following Failed";
    }
}

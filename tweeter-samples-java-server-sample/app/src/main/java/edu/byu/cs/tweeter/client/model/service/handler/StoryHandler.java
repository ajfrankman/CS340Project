package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.service.paged.PagesObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoryHandler extends BackgroundTaskHandler<PagesObserver> {

    public StoryHandler(PagesObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagesObserver observer, Bundle bundle) {
        List<Status> statuses = (List<Status>) bundle.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = bundle.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
        observer.getItemsSucceeded(statuses, hasMorePages);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Get Story failed";
    }
}

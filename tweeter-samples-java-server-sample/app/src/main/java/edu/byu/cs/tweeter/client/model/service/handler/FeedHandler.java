package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.service.paged.PagesObserver;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Message handler (i.e., observer) for GetFeedTask.
 */
public class FeedHandler extends BackgroundTaskHandler<PagesObserver> {


    public FeedHandler(PagesObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagesObserver observer, Bundle bundle) {
        List<Status> statuses = (List<Status>) bundle.getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = bundle.getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.getItemsSucceeded(statuses, hasMorePages);
    }

    @Override
    protected String getFailedMessagePrefix() {
        return "Get Feed Failed";
    }
}

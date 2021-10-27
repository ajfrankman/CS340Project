package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;


public abstract class PagedTask<T> extends AuthorizedTask {



    /**
     * Maximum number of followers to return (i.e., page size).
     */
    protected int limit;
    /**
     * The last follower returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    protected T lastItem;
    /**
     * Message handler that will receive task results.
     */
    private Handler messageHandler;

    private List<T> items;
    private boolean hasMorePages;

    public static final String ITEMS_KEY = "followees";
    public static final String MORE_PAGES_KEY = "more-pages";

    public PagedTask(Handler messageHandler, AuthToken authToken, int limit, T lastItem) {
        super(messageHandler, authToken);
        this.limit = limit;
        this.lastItem = lastItem;

    }

    @Override
    protected void runTask() throws IOException {
        Pair<List<T>, Boolean> pageOfItems = getItems();

        items = pageOfItems.getFirst();
        hasMorePages = pageOfItems.getSecond();

        loadImages(items);
    }

    protected abstract Pair<List<T>, Boolean> getItems();

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    private void loadImages(List<T> followers) throws IOException {
        for (User u : convertItemsToUsers(followers)) {
            BackgroundTaskUtils.loadImage(u);
        }
    }

    protected abstract List<User> convertItemsToUsers(List<T> items);
}


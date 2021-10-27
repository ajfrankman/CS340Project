package edu.byu.cs.tweeter.client.presenter.paged;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.service.UserService;
import edu.byu.cs.tweeter.client.model.service.service.paged.PagesObserver;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagesPresenter<T> implements UserService.UserObserver, PagesObserver<T> {
    protected static final int PAGE_SIZE = 10;
    protected boolean isLoading = false;
    public boolean hasMorePages = true;
    protected final AuthToken authToken;
    protected final User targetUser;
    protected T lastItem;
    protected PagedView<T> view;


    public PagesPresenter(PagedView view, AuthToken authToken, User targetUser) {
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void getItemsSucceeded(List<T> items, boolean hasMorePages) {
        lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
        this.isLoading = false;
        view.setLoading(false);
        view.addItems(items);
        this.hasMorePages = hasMorePages;
    }


    public void loadMoreItems() throws MalformedURLException {
        if (!isLoading && hasMorePages) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            getPageItems();
        }
    }

    @Override
    public void handleFailure(String message) { view.displayErrorMessage(message); }

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    protected abstract void getPageItems();
}

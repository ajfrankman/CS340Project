package edu.byu.cs.tweeter.client.presenter.paged;

import edu.byu.cs.tweeter.client.model.service.service.paged.FeedService;
import edu.byu.cs.tweeter.client.model.service.service.UserService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.StatusView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends StatusPresenter {

    //Constructor
    public FeedPresenter(StatusView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

    @Override
    protected void getPageItems() {
        ((StatusView)view).setLoading(true);
        new FeedService().getFeed(targetUser, PAGE_SIZE, lastItem, this);
    }

    public void goToUser(String alias) {
        new UserService().getUser(authToken, alias, this);
    }


}

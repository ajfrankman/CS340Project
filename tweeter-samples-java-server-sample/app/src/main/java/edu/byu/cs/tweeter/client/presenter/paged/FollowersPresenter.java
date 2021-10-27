package edu.byu.cs.tweeter.client.presenter.paged;

import edu.byu.cs.tweeter.client.model.service.service.paged.FollowerService;
import edu.byu.cs.tweeter.client.model.service.service.UserService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.UserView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends UserPresenter {

    public FollowersPresenter(UserView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

    @Override
    protected void getPageItems() {
        ((UserView)view).setLoading(true);
        new FollowerService().getFollowers(authToken, targetUser, PAGE_SIZE, lastItem, this);
    }

    public void goToUser(String alias) {
        new UserService().getUser(authToken, alias, this);
    }
}

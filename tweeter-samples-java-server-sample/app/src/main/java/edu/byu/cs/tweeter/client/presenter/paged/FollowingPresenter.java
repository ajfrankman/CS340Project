package edu.byu.cs.tweeter.client.presenter.paged;

import edu.byu.cs.tweeter.client.model.service.service.UserService;
import edu.byu.cs.tweeter.client.model.service.service.paged.FollowingService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.UserView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends UserPresenter {

    public FollowingPresenter(UserView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

    @Override
    protected void getPageItems() {
        ((UserView)view).setLoading(true);
        new FollowingService().getFollowing(authToken, targetUser, PAGE_SIZE, lastItem, this);
    }

    public void goToUser(String alias) {
        new UserService().getUser(authToken, alias, this);
    }
}

package edu.byu.cs.tweeter.client.presenter.paged;

import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class UserPresenter extends PagesPresenter<User> {

    public UserPresenter(PagedView<User> view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

}

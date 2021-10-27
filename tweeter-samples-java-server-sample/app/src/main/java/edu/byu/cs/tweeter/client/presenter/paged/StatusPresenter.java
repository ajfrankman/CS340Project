package edu.byu.cs.tweeter.client.presenter.paged;

import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class StatusPresenter extends PagesPresenter<Status> {

    public StatusPresenter(PagedView<Status> view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

}

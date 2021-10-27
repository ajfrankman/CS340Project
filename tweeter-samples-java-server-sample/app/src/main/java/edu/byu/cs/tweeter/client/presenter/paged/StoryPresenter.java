package edu.byu.cs.tweeter.client.presenter.paged;

import edu.byu.cs.tweeter.client.model.service.service.UserService;
import edu.byu.cs.tweeter.client.model.service.service.paged.StoryService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.PagedView;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.StatusView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends StatusPresenter {

    //Constructor
    public StoryPresenter(StatusView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

    @Override
    protected void getPageItems() {
        ((StatusView)view).setLoading(true);
        new StoryService().getStory(targetUser, PAGE_SIZE, lastItem, this);
    }

    public void goToUser(String alias) {
        new UserService().getUser(authToken, alias, this);
    }
}

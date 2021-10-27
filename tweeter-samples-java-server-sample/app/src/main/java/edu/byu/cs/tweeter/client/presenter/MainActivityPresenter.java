package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.service.FollowersCountService;
import edu.byu.cs.tweeter.client.model.service.service.FollowingCountService;
import edu.byu.cs.tweeter.client.model.service.service.IsFollowerService;
import edu.byu.cs.tweeter.client.model.service.service.LogoutService;
import edu.byu.cs.tweeter.client.model.service.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.service.UnfollowService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends SuperPresenter implements UnfollowService.UnfollowObserver,
        FollowService.FollowObserver,
        IsFollowerService.IsFollowerObserver,
        FollowingCountService.GetFollowingCountObserver,
        FollowersCountService.GetFollowersCountObserver,
        LogoutService.LogoutObserver,
        PostStatusService.PostStatusObserver {

    @Override
    public void onFollowSuccess() {
        ((MainView)view).updateSelectedUserFollowingAndFollowers();
        ((MainView)view).updateFollowButton(false);
    }

    public MainActivityPresenter (MainView view) {

        super(view);
    }

    public void unfollow(AuthToken authToken, User user) {
        new UnfollowService().Unfollow(this, authToken, user);
    }

    public void follow(AuthToken authToken, User user) {
        new FollowService().Follow(this, authToken, user);
    }

    public void isFollower(User selectedUser) {
        new IsFollowerService().IsFollower(this, selectedUser);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser) {
        new FollowingCountService().getFollowingCount(this, authToken, selectedUser);
    }

    public void getFollowersCount(AuthToken authToken, User selectedUser) {
        new FollowersCountService().getFollowersCount(this, authToken, selectedUser);
    }

    public void logout() {
        new LogoutService().logout(this);
    }

    public PostStatusService getPostStatusService() {
        return new PostStatusService();
    }

    public void postStatus(String post) {
        getPostStatusService().postStatus(post, this);
    }

    @Override
    public void postStatusSucceeded(String message) {
        ((MainView)view).makeToast(message);
    }

    @Override
    public void logoutSucceeded() {
        ((MainView)view).logoutUser();
    }

    @Override
    public void getFollowersCountSucceeded(int count) {
        ((MainView)view).setFollowerCount(count);

    }

    @Override
    public void getFollowingCountSucceeded(int count) {
        ((MainView)view).setFolloweeCount(count);
    }

    @Override
    public void isFollowerSuccess(boolean isFollower) {
        ((MainView)view).isFollowingButton(isFollower);
    }

    @Override
    public void UnfollowSucceeded() {
        ((MainView)view).updateSelectedUserFollowingAndFollowers();
        ((MainView)view).updateFollowButton(true);
    }
}

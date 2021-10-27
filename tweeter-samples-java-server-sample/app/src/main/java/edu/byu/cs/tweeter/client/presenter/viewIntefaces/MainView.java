package edu.byu.cs.tweeter.client.presenter.viewIntefaces;

public interface MainView extends SuperView {
    void displayInfoMessage(String message);
    void updateSelectedUserFollowingAndFollowers();
    void updateFollowButton(boolean removed);
    void makeToast(String toast);
    void isFollowingButton(boolean isFollower);
    void setFolloweeCount(int count);
    void setFollowerCount(int count);
    void logoutUser();
}

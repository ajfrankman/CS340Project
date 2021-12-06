package edu.byu.cs.tweeter.model.domain;

import java.util.List;

public class FeedUpdater extends SimpleStatus{
    List<String> followers;

    public FeedUpdater(List<String> followers) {
        this.followers = followers;
    }

    public FeedUpdater(List<String> followers, SimpleStatus ss) {
        super(ss.post, ss.alias, ss.datetime, ss.urls, ss.mentions);
        this.followers = followers;
    }

    public FeedUpdater(String post, String alias, String datetime, List<String> urls, List<String> mentions, List<String> followers) {
        super(post, alias, datetime, urls, mentions);
        this.followers = followers;
    }

    public FeedUpdater(Status status, List<String> followers) {
        super(status);
        this.followers = followers;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}

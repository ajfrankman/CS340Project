package edu.byu.cs.tweeter.model.domain;

import java.util.List;

public class SimpleStatus {
    public String post;
    public String alias;
    public String datetime;
    public List<String> urls;
    public List<String> mentions;

    public SimpleStatus() {

    }

    public SimpleStatus(String post, String alias, String datetime, List<String> urls, List<String> mentions) {
        this.post = post;
        this.alias = alias;
        this.datetime = datetime;
        this.urls = urls;
        this.mentions = mentions;
    }

    public SimpleStatus(Status status) {
        this.post = status.post;
        this.alias = status.getUser().getAlias();
        this.datetime = status.datetime;
        this.urls = status.urls;
        this.mentions = status.mentions;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }
}

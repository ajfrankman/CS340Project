package edu.byu.cs.tweeter.client.presenter.viewIntefaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedView<U> extends SuperView {
    void navigateToUser(User user);
    void setLoading(boolean value);
    void addItems(List<U> items);
}

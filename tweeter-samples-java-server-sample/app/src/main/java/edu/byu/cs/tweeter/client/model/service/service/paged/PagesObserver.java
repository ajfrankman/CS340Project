package edu.byu.cs.tweeter.client.model.service.service.paged;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.service.ServiceObserver;

public interface PagesObserver<T> extends ServiceObserver {
    void getItemsSucceeded(List<T> items, boolean hasMorePages);
}

package edu.byu.cs.tweeter.client.model.service.service;

public interface ServiceObserver {
    void handleFailure(String message);
}

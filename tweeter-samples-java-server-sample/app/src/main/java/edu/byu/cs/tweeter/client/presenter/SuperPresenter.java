package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.service.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.SuperView;

public abstract class SuperPresenter implements ServiceObserver {

    SuperView view;

    public SuperPresenter(SuperView view) {
        this.view = view;
    }


    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(message);
    }

}

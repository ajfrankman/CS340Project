package edu.byu.cs.tweeter.client.presenter.viewIntefaces;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticationView  extends SuperView{

    void clearErrorMessage();
    void clearInfoMessage();
    void displayInfoMessage(String message);
    void navigateToUser(User user);

}

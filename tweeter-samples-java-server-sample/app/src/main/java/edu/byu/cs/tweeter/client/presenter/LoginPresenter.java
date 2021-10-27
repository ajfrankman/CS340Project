package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.service.LoginService;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.AuthenticationView;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.SuperView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends SuperPresenter implements LoginService.LoginObserver {

    public LoginPresenter(AuthenticationView view) {
        super(view);
    }

    @Override
    public void logoutSucceeded(AuthToken authToken, User user) {
        ((AuthenticationView)view).navigateToUser(user);
        ((AuthenticationView)view).clearErrorMessage();
        ((AuthenticationView)view).displayInfoMessage("Hello" + user.alias);
    }

    private String validateLogin(String alias, String password) {
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) return "Password cannot be empty.";
        return null;
    }

    public void login(String alias, String password) {
        ((AuthenticationView)view).clearErrorMessage();
        ((AuthenticationView)view).clearInfoMessage();

        String message = validateLogin(alias, password);
        if(message == null) {
            ((AuthenticationView)view).displayInfoMessage("Logging In...");
            new LoginService().login(alias, password, this);
        } else view.displayErrorMessage("Login Failed: " + message);
    }
}

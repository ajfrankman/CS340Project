package edu.byu.cs.tweeter.client.model.service.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.handler.LoginHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginService extends SuperService{


    public interface LoginObserver extends ServiceObserver{
        void logoutSucceeded(AuthToken authToken, User user);
    }

    public void login(String alias, String password, LoginObserver observer) {
        // Run a LoginTask to login the user
        // Send the login request.
        LoginTask task = new LoginTask(alias, password, new LoginHandler(observer));
        execute(task);
    }

}

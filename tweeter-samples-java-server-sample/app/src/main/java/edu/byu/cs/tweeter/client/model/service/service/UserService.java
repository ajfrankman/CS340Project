package edu.byu.cs.tweeter.client.model.service.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.model.service.handler.UserHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends SuperService {

    public interface UserObserver extends ServiceObserver{
        void getUserSucceeded(User user);
    }

    public void getUser(AuthToken authToken, String alias, UserObserver observer) {
        GetUserTask task = new GetUserTask(authToken, alias, new UserHandler(observer));
        execute(task);

    }
}

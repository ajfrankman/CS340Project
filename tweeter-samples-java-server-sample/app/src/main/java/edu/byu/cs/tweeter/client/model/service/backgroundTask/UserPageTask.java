package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;

public abstract class UserPageTask extends PagedTask<User> {

    /**
     * The user whose following is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    protected User targetUser;

    public UserPageTask(Handler messageHandler, AuthToken authToken, int limit, User lastItem, User targetUser) {
        super(messageHandler, authToken, limit, lastItem);
        this.targetUser = targetUser;

    }

    @Override
    protected List<User> convertItemsToUsers(List<User> items) {
        return items;
    }
}

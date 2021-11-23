package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.model.util.FakeData;

public abstract class StatusPageTask extends PagedTask<Status> {
    /**
     * The user whose story is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    protected User targetUser;

    public StatusPageTask(Handler messageHandler, AuthToken authToken, int limit, Status lastItem, User targetUser) {
        super(messageHandler, authToken, limit, lastItem);
        this.targetUser = targetUser;
    }

    @Override
    protected List<User> convertItemsToUsers(List<Status> items) {
        List<User> users = items.stream().map(x -> x.user).collect(Collectors.toList());
        return users;

    }
}

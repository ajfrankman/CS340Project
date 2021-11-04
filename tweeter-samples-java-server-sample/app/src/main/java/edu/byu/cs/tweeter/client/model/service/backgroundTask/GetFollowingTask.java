package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends UserPageTask {
    private static final String LOG_TAG = "GetFollowingTask";

    static final String URL_PATH = "/getfollowing";
    private ServerFacade serverFacade;

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(messageHandler, authToken, limit, lastFollowee, targetUser);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        FollowingRequest followingRequest;
        if (lastItem == null) {
            followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), limit, null);
        } else {
            followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), limit, lastItem.getAlias());

        }
        FollowingResponse followingResponse;
        Pair<List<User>, Boolean> responsePair = null;
        try {
            followingResponse = getServerFacade().getFollowees(followingRequest, URL_PATH);
             responsePair = new Pair<>(followingResponse.getFollowees(), followingResponse.getHasMorePages());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
        
        return responsePair;
    }
}

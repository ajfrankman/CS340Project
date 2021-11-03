package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;
/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends UserPageTask {
    private static final String LOG_TAG = "GetFollowersTask";

    //TODO Change this to /getfollowers
    static final String URL_PATH = "/getfollowing";
    private ServerFacade serverFacade;

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(messageHandler, authToken, limit, lastFollower, targetUser);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        //TODO change to getFollowers through this function.
        FollowingRequest followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), limit, lastItem.getAlias());
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

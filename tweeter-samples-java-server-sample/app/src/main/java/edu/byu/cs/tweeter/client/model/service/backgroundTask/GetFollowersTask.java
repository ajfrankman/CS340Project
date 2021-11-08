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
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;
/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends UserPageTask {
    private static final String LOG_TAG = "GetFollowersTask";

    //TODO Change this to /getfollowers
    static final String URL_PATH = "/getfollowers";


    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(messageHandler, authToken, limit, lastFollower, targetUser);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        FollowersRequest followersRequest;
        if (lastItem == null) {
            followersRequest = new FollowersRequest(authToken, targetUser.getAlias(), limit, null);
        } else {
            followersRequest = new FollowersRequest(authToken, targetUser.getAlias(), limit, lastItem.getAlias());
        }
        FollowersResponse followersResponse = null;
        Pair<List<User>, Boolean> responsePair = null;
        try {

            followersResponse = getServerFacade().getFollowers(followersRequest, URL_PATH);
            responsePair = new Pair<>(followersResponse.getFollowers(), followersResponse.getHasMorePages());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }

        return responsePair;
    }
}

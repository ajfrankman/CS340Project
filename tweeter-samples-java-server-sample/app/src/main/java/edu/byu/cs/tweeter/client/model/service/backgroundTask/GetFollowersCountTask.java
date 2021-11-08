package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends CountTask {

    private static final String URL_PATH = "/getfollowerscount";
    ServerFacade serverFacade;

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler,authToken, targetUser);
    }

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    public int getCount() {
        GetFollowersCountRequest getFollowersCountRequest = new GetFollowersCountRequest(this.authToken, this.targetUser);
        GetFollowersCountResponse getFollowersCountResponse = null;

        try {
            getFollowersCountResponse = getServerFacade().getFollowersCount(getFollowersCountRequest, URL_PATH);
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        return getFollowersCountResponse.getCount();
    }
}
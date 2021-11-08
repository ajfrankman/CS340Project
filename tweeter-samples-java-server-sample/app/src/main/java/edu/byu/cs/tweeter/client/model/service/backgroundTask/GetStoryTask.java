package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends StatusPageTask {
    private static final String LOG_TAG = "GetStoryTask";

    static final String URL_PATH = "/getstories";


    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(messageHandler, authToken, limit, lastStatus, targetUser);
    }


    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        StoriesRequest storiesRequest;
        if (lastItem == null) {
            storiesRequest = new StoriesRequest(null, limit);
        } else {
            storiesRequest = new StoriesRequest(lastItem, limit);
        }

        StoriesResponse storiesResponse;
        Pair<List<Status>, Boolean> responsePair = null;

        try {
            storiesResponse = getServerFacade().getStories(storiesRequest, URL_PATH);
            responsePair = new Pair<>(storiesResponse.getStories(), storiesResponse.getHasMorePages());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }

        return responsePair;
    }
}
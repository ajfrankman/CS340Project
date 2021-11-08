package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends StatusPageTask {
    private static final String LOG_TAG = "GetFeedTask";

    static final String URL_PATH = "/getfeed";


    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(messageHandler, authToken, limit, lastStatus, targetUser);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        FeedRequest feedRequest;
        if (lastItem == null) {
            feedRequest = new FeedRequest(null, limit);
        } else {
            feedRequest = new FeedRequest(lastItem, limit);
        }

        FeedResponse feedResponse;
        Pair<List<Status>, Boolean> responsePair = null;

        try {
            feedResponse = getServerFacade().getFeed(feedRequest, URL_PATH);
            responsePair = new Pair<>(feedResponse.getFeed(), feedResponse.getHasMorePages());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }

        return responsePair;
    }
}

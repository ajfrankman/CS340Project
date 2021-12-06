package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.FeedUpdater;
import edu.byu.cs.tweeter.model.domain.SimpleStatus;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.service.UserService;

public class UpdateFeeds extends BaseHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Gson gson = new Gson();
            FeedUpdater feedUpdater = gson.fromJson(msg.getBody(), FeedUpdater.class);
            UserService userService = new UserService(currentFactory);
            userService.addStatusToFeeds(feedUpdater);
        }
        return null;
    }
}

package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.Request;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.domain.SimpleStatus;
import edu.byu.cs.tweeter.server.service.UserService;

public class PostUpdateFeedMessages extends BaseHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Gson gson = new Gson();
            SimpleStatus simpleStatus = gson.fromJson(msg.getBody(), SimpleStatus.class);
            System.out.println("This is an ALIAS from a deserialized json string:" + simpleStatus.getAlias());
            UserService userService = new UserService(currentFactory);
            userService.updateFollowersStatus(simpleStatus);
            // Grab 25 user Alias at a time and throw them into a SQS Update Feed Queue
        }
        return null;
    }
    // Get x follows from
}

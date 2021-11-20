package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class IsFollowerHandler extends BaseHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {

    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest isFollowerRequest, Context context) {
        FollowService followService = new FollowService(currentFactory);
        return followService.isFollower(isFollowerRequest);
    }
}

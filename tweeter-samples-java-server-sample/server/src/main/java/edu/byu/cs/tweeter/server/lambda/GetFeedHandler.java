package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.service.StatusPageService;

public class GetFeedHandler implements RequestHandler<FeedRequest, FeedResponse> {


    @Override
    public FeedResponse handleRequest(FeedRequest request, Context context) {
        StatusPageService statusPageService = new StatusPageService();
        return statusPageService.getFeed(request);
    }
}

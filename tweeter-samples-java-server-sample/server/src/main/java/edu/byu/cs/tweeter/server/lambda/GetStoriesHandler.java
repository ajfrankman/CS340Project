package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.server.service.StatusPageService;

public class GetStoriesHandler implements RequestHandler<StoriesRequest, StoriesResponse> {

    @Override
    public StoriesResponse handleRequest(StoriesRequest request, Context context) {
        StatusPageService statusPageService = new StatusPageService();
        return statusPageService.getStories(request);
    }
}
package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class PostStatusHandler extends BaseHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    @Override
    public PostStatusResponse handleRequest(PostStatusRequest postStatusRequest, Context context) {
        UserService userService = new UserService(currentFactory);
        return userService.postStatus(postStatusRequest);
    }
}

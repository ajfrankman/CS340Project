package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class GetFollowingHandler extends BaseHandler implements RequestHandler<FollowingRequest, FollowingResponse> {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the followees.
     */
    @Override
    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        FollowService service = new FollowService(currentFactory);
        return service.getFollowing(request);
    }


    public static void main(String[] args) {
        AuthToken authToken = new AuthToken("536eeb9d-82f3-43cf-bf12-9bd3b7b68f00", "2021-12-06T00:14:41.671907", "@superUser");
        FollowingRequest followingRequest = new FollowingRequest(authToken, "@superUser", 10, null);
        DynamoDBFactory daoFactory = DynamoDBFactory.getInstance();
        FollowService followService = new FollowService(daoFactory);
        FollowingResponse followingResponse = followService.getFollowing(followingRequest);
        System.out.println("hello");
    }
}

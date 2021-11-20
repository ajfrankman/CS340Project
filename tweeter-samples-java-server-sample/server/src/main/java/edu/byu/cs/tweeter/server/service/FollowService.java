package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    DAOFactory currentFactory;

    public FollowService(DAOFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if (request.getLimit() < 1 || request.getFollowerAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().getFollowing(request);
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowersResponse getFollowers(FollowersRequest request) {
        if (request.getLimit() < 1 || request.getFolloweeAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().getFollowers(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().isFollower(request);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if (request.getAuthToken() == null || request.getTargetUser() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().getFollowersCount(request);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if (request.getAuthToken() == null || request.getTargetUser() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().getFollowingCount(request);
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getAuthToken() == null || request.getFollowee() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().follow(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getAuthToken() == null || request.getFollowee() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return currentFactory.getFollowDAO().unfollow(request);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }
}

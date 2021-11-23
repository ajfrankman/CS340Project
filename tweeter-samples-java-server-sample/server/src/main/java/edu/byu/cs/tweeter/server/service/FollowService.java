package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
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
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.util.Pair;
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

    public FollowingResponse getFollowing(FollowingRequest request) {
        if (request.getLimit() < 1 || request.getFollowerAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid FollowingRequest object");
        }

        // Get following alias list and has more page
        Pair<List<String>, Boolean> resultPair = currentFactory
                .getFollowDAO()
                .getFollowingAlias(
                        request.getFollowerAlias(),
                        request.getLastFolloweeAlias(),
                        request.getLimit()
                );

        List<String> aliasList = resultPair.getFirst();
        Boolean hasMorePages = resultPair.getSecond();

        // Get users from aliass
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < aliasList.size(); i++) {
            userList.add(currentFactory.getUserDAO().getUser(aliasList.get(i)));
        }

        return new FollowingResponse(userList, hasMorePages);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if (request.getLimit() < 1 || request.getFolloweeAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid FollowersRequest object");
        }
        // Get follower alias list and has more page
        Pair<List<String>, Boolean> resultPair = currentFactory
                .getFollowDAO()
                .getFollowersAlias(
                        request.getFolloweeAlias(),
                        request.getLastFollowerAlias(),
                        request.getLimit()
                );

        List<String> aliasList = resultPair.getFirst();
        Boolean hasMorePages = resultPair.getSecond();

        // Get users from aliass
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < aliasList.size(); i++) {
            userList.add(currentFactory.getUserDAO().getUser(aliasList.get(i)));
        }

        // Build Response
        return new FollowersResponse(userList, hasMorePages);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // Check if user is follower
        Boolean isFollower = currentFactory.getFollowDAO().isFollower(request.getFollower().getAlias(), request.getFollowee().getAlias());

        return new IsFollowerResponse(isFollower);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if (request.getAuthToken() == null || request.getTargetUser() == null) {
            throw new RuntimeException("Invalid request object");
        }
        if (!currentFactory.getAuthDAO().goodAuthToken(request.getAuthToken())) {
            return new GetFollowersCountResponse("invalid AuthToken");
        }


        // Return the followers count of a given user.
        int followersCount = currentFactory.getUserDAO().getUserFollowersCount(request.getTargetUser().getAlias());

        return new GetFollowersCountResponse(followersCount);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if (request.getAuthToken() == null || request.getTargetUser() == null) {
            throw new RuntimeException("Invalid request object");
        }
        if (!currentFactory.getAuthDAO().goodAuthToken(request.getAuthToken())) {
            return new GetFollowingCountResponse("invalid AuthToken");
        }

        // Return the following count of a given user.
        int followingCount = currentFactory.getUserDAO().getUserFollowingCount(request.getTargetUser().getAlias());

        return new GetFollowingCountResponse(followingCount);
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getAuthToken() == null || request.getFollowee() == null) {
            throw new RuntimeException("Invalid FollowRequest object");
        }
        if (!currentFactory.getAuthDAO().goodAuthToken(request.getAuthToken())) {
            return new FollowResponse("invalid AuthToken");
        }

        // add follow relationship to follow table
        currentFactory.getFollowDAO().follow(request.getAuthToken().getUserAlias(), request.getFollowee().getAlias());
        // increment currentUser followingCount
        currentFactory.getUserDAO().updateFollowingCount(request.getAuthToken().getUserAlias(), 1);
        // increment other user's followersCount
        currentFactory.getUserDAO().updateFollowersCount(request.getFollowee().getAlias(), 1);

        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getAuthToken() == null || request.getFollowee() == null) {
            throw new RuntimeException("Invalid UnfollowRequest object");
        }
        if (!currentFactory.getAuthDAO().goodAuthToken(request.getAuthToken())) {
            return new UnfollowResponse("invalid AuthToken");
        }

        // add follow relationship to follow table
        currentFactory.getFollowDAO().unfollow(request.getAuthToken().getUserAlias(), request.getFollowee().getAlias());
        // decrement currentUser followingCount
        currentFactory.getUserDAO().updateFollowingCount(request.getAuthToken().getUserAlias(), -1);
        // decrement other user's followersCount
        currentFactory.getUserDAO().updateFollowersCount(request.getFollowee().getAlias(), -1);

        return new UnfollowResponse();
    }
}

package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import org.apache.commons.logging.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.FeedUpdater;
import edu.byu.cs.tweeter.model.domain.SimpleStatus;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dynamo.DynamoDBFactory;
import edu.byu.cs.tweeter.server.dynamo.FollowDAO;
import edu.byu.cs.tweeter.server.dynamo.UserDAO;
import edu.byu.cs.tweeter.server.factoryinterfaces.DAOFactory;

public class UserService {

    DAOFactory currentFactory;

    // TODO check authToken for anything that needs to.

    public UserService(DAOFactory currentFactory) {
        this.currentFactory = currentFactory;
    }

    public LoginResponse login(LoginRequest request) {
        // valid request check
        if (request.getPassword() == null || request.getUsername() == null) {
            throw new RuntimeException("Invalid LoginRequest object");
        }

        // getUser()
        User user = currentFactory.getUserDAO().getUser(request.getUsername());
        // getAuthToken()
        AuthToken authToken = currentFactory.getAuthDAO().generateAuthToken(request.getUsername());
        // Create Response()
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        // valid request check
        if (request.getAlias() == null || request.getPassword() == null || request.getFirstName() == null || request.getLastName() == null || request.getImageBytesBase64() == null) {
            throw new RuntimeException("Invalid RegisterRequest object");
        }

        // add image()
        URL url = currentFactory.getUserDAO().addUserImage(request.getAlias(), request.getImageBytesBase64());
        // add User()
        currentFactory.getUserDAO().addUser(request.getAlias(), request.getFirstName(), request.getLastName(), request.getPassword(), url.toString());
        // getUser() **maybe just create here without grabbing?**
        User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getAlias(), url.toString());
        // generateAuthToken()
        AuthToken authToken = currentFactory.getAuthDAO().generateAuthToken(request.getAlias());
        // Create Response
        return new RegisterResponse(registeredUser, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        // valid request check
        if (request.getAuthToken() == null) {
            throw new RuntimeException("Invalid LogoutRequest object");
        }

        // remove authToken from table
        currentFactory.getAuthDAO().removeAuthToken(request.getAuthToken());
        // Send logout Response
        return new LogoutResponse();
    }

    public UserResponse getUser(UserRequest request) {
        if (request.getAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid UserRequest object");
        }
        // Authentication
        if (!currentFactory.getAuthDAO().goodAuthToken(request.getAuthToken())) {
            return new UserResponse("invalid AuthToken");
        }
        // getUser()
        User user = currentFactory.getUserDAO().getUser(request.getAlias());
        // Create Response()
        return new UserResponse(user);
    }

    public void updateFollowersStatus(SimpleStatus simpleStatus) {
        String lastFollowerAlias = null;
        Boolean hasMorePages = true;
        while (hasMorePages) {
            // TODO grab 10000 at once instead
            Pair<List<String>, Boolean> followersBatchResult =
                    currentFactory.getFollowDAO().getFollowersAlias(simpleStatus.getAlias(), lastFollowerAlias, 25);
            hasMorePages = followersBatchResult.getSecond();
            if(followersBatchResult.getFirst().size() > 0) {
                // set for next getFollowersAlias call
                lastFollowerAlias = followersBatchResult.getFirst().get(followersBatchResult.getFirst().size() - 1);
                System.out.println("LAST FOLLOWER ALIAS: " + lastFollowerAlias);
                // Create an object to send to UpdateFeedQueue
                Gson gson = new Gson();
                FeedUpdater feedUpdater = new FeedUpdater(followersBatchResult.getFirst(), simpleStatus);
                String jsonFeedUpdater = gson.toJson(feedUpdater);

                // Add to the queue
                String queueURL = "https://sqs.us-west-2.amazonaws.com/375475139933/UpdateFeedQueue";
                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueURL)
                        .withMessageBody(jsonFeedUpdater);
                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult sendMessageResult = sqs.sendMessage(send_msg_request);
            }
        }
    }

//    public static void main(String[] args) {
//        DynamoDBFactory daoFactory = DynamoDBFactory.getInstance();
//        String lastFollowerAlias = null;
////        daoFactory.getUserDAO();
//        Pair<List<String>, Boolean> followersBatchResult0 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult0.getFirst().get(followersBatchResult0.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult0.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult1 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult1.getFirst().get(followersBatchResult1.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult1.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult2 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult2.getFirst().get(followersBatchResult2.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult2.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult3 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult3.getFirst().get(followersBatchResult3.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult3.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult4 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult4.getFirst().get(followersBatchResult4.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult4.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult5 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult5.getFirst().get(followersBatchResult5.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult5.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult6 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult6.getFirst().get(followersBatchResult6.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult6.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult7 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult7.getFirst().get(followersBatchResult7.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult7.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult8 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult8.getFirst().get(followersBatchResult8.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult8.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult9 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult9.getFirst().get(followersBatchResult9.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult9.toString());
//
//        Pair<List<String>, Boolean> followersBatchResult10 =
//                daoFactory.getFollowDAO().getFollowersAlias("@superGal", lastFollowerAlias, 10);
//        lastFollowerAlias = followersBatchResult10.getFirst().get(followersBatchResult10.getFirst().size()-1);
//        System.out.println("list: " + followersBatchResult10.toString());
//    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        if (postStatusRequest.getStatus() == null || postStatusRequest.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        if (!currentFactory.getAuthDAO().goodAuthToken(postStatusRequest.getAuthToken())) {
            return new PostStatusResponse("invalid AuthToken");
        }
        // create item to add to table
        Item status = new Item().withPrimaryKey("user_handle", postStatusRequest.getStatus().getUser().getAlias(), "date", postStatusRequest.getStatus().getDate())
                .with("post", postStatusRequest.getStatus().getPost())
                .with("mentions", postStatusRequest.getStatus().getMentions())
                .with("urls", postStatusRequest.getStatus().getUrls());
        // add status to story table
        currentFactory.getStoryDAO().addStatus(status);

        // TODO write post to PostStatusQueue. That will call a function called PostUpdateFeedMessages. Return now.

        Gson gson = new Gson();
        SimpleStatus simpleStatus = new SimpleStatus(postStatusRequest.getStatus());

        String statusString = gson.toJson(simpleStatus);
        String queueURL = "https://sqs.us-west-2.amazonaws.com/375475139933/PostStatusQueue";


        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueURL)
                .withMessageBody(statusString);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult sendMessageResult = sqs.sendMessage(send_msg_request);

        return new PostStatusResponse();
//        Pair<List<String>, Boolean> resultPair = currentFactory.getFollowDAO().getFollowersAlias(postStatusRequest.getStatus().getUser().getAlias(), null, 10);
//        List<String> aliasList = resultPair.getFirst();
//
//        System.out.println("aliasList size: " + aliasList.size());
//
//        for (int i = 0; i < aliasList.size(); i++) {
//            Item feedStatus = new Item().withPrimaryKey("user_handle", aliasList.get(i), "date", postStatusRequest.getStatus().getDate())
//                    .with("postedBy", postStatusRequest.getStatus().getUser().getAlias())
//                    .with("post", postStatusRequest.getStatus().getPost())
//                    .with("mentions", postStatusRequest.getStatus().getMentions())
//                    .with("urls", postStatusRequest.getStatus().getUrls());
//
//            currentFactory.getFeedDAO().addStatus(feedStatus);
//        }
    }

    public void addStatusToFeeds(FeedUpdater feedUpdater) {
        currentFactory.getFeedDAO().addStatusBatch(feedUpdater);
    }

    public static void main(String[] args) {
// Get instance of DAOs by way of the Abstract Factory Pattern
        DynamoDBFactory daoFactory = DynamoDBFactory.getInstance();
        daoFactory.getUserDAO();
        UserDAO userDAO = daoFactory.getUserDAO();
        FollowDAO followDAO = daoFactory.getFollowDAO();


        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

        // Iterate over the number of users you will create
        for (int i = 1; i <= 100; i++) {

            String name = "Gal " + i;
            String alias = "@gal" + i;


            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User(name, "lastName", alias, MALE_IMAGE_URL);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, "@superGal");
        }        // UserDAO add batch
    }
}

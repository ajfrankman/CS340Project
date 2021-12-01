package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import org.apache.commons.logging.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
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

        Pair<List<String>, Boolean> resultPair = currentFactory.getFollowDAO().getFollowersAlias(postStatusRequest.getStatus().getUser().getAlias(), null, 10);
        List<String> aliasList = resultPair.getFirst();

        System.out.println("aliasList size: " + aliasList.size());

        for (int i = 0; i < aliasList.size(); i++) {
            Item feedStatus = new Item().withPrimaryKey("user_handle", aliasList.get(i), "date", postStatusRequest.getStatus().getDate())
                    .with("postedBy", postStatusRequest.getStatus().getUser().getAlias())
                    .with("post", postStatusRequest.getStatus().getPost())
                    .with("mentions", postStatusRequest.getStatus().getMentions())
                    .with("urls", postStatusRequest.getStatus().getUrls());

            currentFactory.getFeedDAO().addStatus(feedStatus);
        }

        return new PostStatusResponse();
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
        for (int i = 1; i <= 10000; i++) {

            String name = "Guy " + i;
            String alias = "@guy" + i;


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
            followDAO.addFollowersBatch(followers, "@superUser");
        }        // UserDAO add batch
    }
}

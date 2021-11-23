package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import org.apache.commons.logging.Log;

import java.net.URL;
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

        // add User()
        currentFactory.getUserDAO().addUser(request.getAlias(), request.getFirstName(), request.getLastName(), request.getPassword());
        // add image()
        URL url = currentFactory.getUserDAO().addUserImage(request.getAlias(), request.getImageBytesBase64());
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
        // getUser()
        User user = currentFactory.getUserDAO().getUser(request.getAlias());
        // Create Response()
        return new UserResponse(user);
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        if (postStatusRequest.getStatus() == null || postStatusRequest.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        // create item to add to table
        Item status = new Item().withPrimaryKey("user_handle", postStatusRequest.getStatus().getUser().getAlias(), "date", postStatusRequest.getStatus().getDate())
                .with("post", postStatusRequest.getStatus().getPost())
                .with("mentions", postStatusRequest.getStatus().getMentions().toString())
                .with("urls", postStatusRequest.getStatus().getUrls().toString());
        // add status to story table
        currentFactory.getStoryDAO().addStatus(status);

        Pair<List<String>, Boolean> resultPair = currentFactory.getFollowDAO().getFollowersAlias(postStatusRequest.getStatus().getUser().getAlias(), null, 10);
        List<String> aliasList = resultPair.getFirst();

        for (int i = 0; i < aliasList.size(); i++) {
            Item feedStatus = new Item().withPrimaryKey("user_handle", aliasList.get(i), "date", postStatusRequest.getStatus().getDate())
                    .with("postedBy", postStatusRequest.getStatus().getUser().getAlias())
                    .with("post", postStatusRequest.getStatus().getPost())
                    .with("mentions", postStatusRequest.getStatus().getMentions().toString())
                    .with("urls", postStatusRequest.getStatus().getUrls().toString());

            currentFactory.getFeedDAO().addStatus(feedStatus);
        }

        return new PostStatusResponse();
    }
}

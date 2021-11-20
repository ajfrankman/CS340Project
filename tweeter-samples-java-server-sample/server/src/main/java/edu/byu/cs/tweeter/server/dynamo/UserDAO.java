package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

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
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.server.factoryinterfaces.UserDAOInterface;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;

public class UserDAO implements UserDAOInterface {

    public LoginResponse login(LoginRequest request) {
        if (request.getPassword() == null || request.getUsername() == null) {
            throw new RuntimeException("Invalid request object");
        }
        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getAlias() == null || request.getPassword() == null || request.getFirstName() == null || request.getLastName() == null || request.getImageBytesBase64() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // Add user to table
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("users");
        try {
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("user_handle", "@" + request.getAlias())
                    .with("firstName", request.getFirstName())
                    .with("lastName", request.getLastName())
                    .with("password", request.getPassword()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Add Image to s3 Bucket
        URL url = null;
        try {
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();

            // Create user profile image file to upload to s3.
            String fileName = String.format("%s_profile_image", request.getAlias());

            // Get image bytes.
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageBytesBase64());

            // Set image metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/jpeg");

            // Upload image (Setting image to be publicly accessible).
            PutObjectRequest fileRequest = new PutObjectRequest("ppictures", fileName, new ByteArrayInputStream(imageBytes), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(fileRequest);

            url = s3.getUrl("ppictures", fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getAlias(), url.toString());
//        User registeredUser = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(registeredUser, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        // TODO: Replace with real Implementation
        return new LogoutResponse();
    }

    public UserResponse getUser(UserRequest request) {
        if (request.getAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        User user = getFakeData().findUserByAlias(request.getAlias());
        return new UserResponse(user);
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        // TODO: Handle the status and stuff
        if (postStatusRequest.getStatus() == null || postStatusRequest.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }
        return new PostStatusResponse();
    }




    /**
     * Returns the dummy user to be returned by the login/register operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

//    Status getDummyStatus() {
//        return getFakeData().getPageOfStatus(null, 1).getFirst().get(0);
//    }

    /**
     * Returns the dummy auth token to be returned by the login/register operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}

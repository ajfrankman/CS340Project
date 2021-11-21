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
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.Get;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class UserDAO implements UserDAOInterface {

    // TODO remove when no longer used
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";



    public LoginResponse login(LoginRequest request) {
        if (request.getPassword() == null || request.getUsername() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // get the user table
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        // get photo link from s3
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        String fileName = String.format("%s_profile_image", request.getUsername());
        URL url = s3.getUrl("ppictures", fileName);

        System.out.println("filename: " + fileName);

        // get User info
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_handle", request.getUsername());
        User user;
        try {
            Item outcome = userTable.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            user = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("user_handle"), url.toString());
        } catch (Exception e) {
            System.err.println("Unable to read item");
            throw e;
        }

        // Generate Authtoken
        LocalDateTime localDateTime = LocalDateTime.now();
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), localDateTime.toString());
        // Put authtoken in table
        Table authTable = dynamoDB.getTable("auth_table");
        try {
            PutItemOutcome outcome = authTable.putItem(new Item().withPrimaryKey("authtoken", authToken.getToken()).with("time", authToken.getDatetime()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

//        AuthToken authToken = getDummyAuthToken();

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
                    .withPrimaryKey("user_handle", request.getAlias())
                    .with("firstName", request.getFirstName())
                    .with("lastName", request.getLastName())
                    .with("password", request.getPassword()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
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

        // Don't bother grabbing the user from table. This is faster.
        User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getAlias(), url.toString());

        // Generate Authtoken
        LocalDateTime localDateTime = LocalDateTime.now();
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), localDateTime.toString());
        // Put authtoken in table
        Table authTable = dynamoDB.getTable("auth_table");
        try {
            PutItemOutcome outcome = authTable.putItem(new Item().withPrimaryKey("authtoken", authToken.getToken()).with("time", authToken.getDatetime()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        return new RegisterResponse(registeredUser, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // TODO: remove authtoken
        return new LogoutResponse();
    }

    public UserResponse getUser(UserRequest request) {
        if (request.getAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // get the user table
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        // get photo link from s3
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        String fileName = String.format("%s_profile_image", request.getAlias());
        URL url = s3.getUrl("ppictures", fileName);

        System.out.println("filename: " + fileName);

        // get User info
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_handle", request.getAlias());
        User user;
        try {
            Item outcome = userTable.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            user = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("user_handle"), url.toString());
        } catch (Exception e) {
            System.err.println("Unable to read item");
            throw e;
        }

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

package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

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
import edu.byu.cs.tweeter.server.factoryinterfaces.UserDAOInterface;

public class UserDAO implements UserDAOInterface {

    // TODO make sure all authorized tasks check for an authtoken

    public LoginResponse login(LoginRequest request) {
        if (request.getPassword() == null || request.getUsername() == null) {
            throw new RuntimeException("Invalid request object");
        }

        return new LoginResponse(getUser(request.getUsername()), generateAuthToken(request.getUsername()));
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getAlias() == null || request.getPassword() == null || request.getFirstName() == null || request.getLastName() == null || request.getImageBytesBase64() == null) {
            throw new RuntimeException("Invalid request object");
        }

        addUser(request);

        // TODO if this fails remove user?
        URL url = addUserImage(request);

        // Don't bother grabbing the user from table. This is cheaper/faster.
        User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getAlias(), url.toString());

        return new RegisterResponse(registeredUser, generateAuthToken(request.getAlias()));
    }

    public LogoutResponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // Don't need to generate auth to log out.
//        LocalDateTime localDateTime = LocalDateTime.now();
//        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), localDateTime.toString());

        // Remove authtoken from table
        Table authTable = dynamoDB.getTable("auth_table");
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(new PrimaryKey("authtoken", request.getAuthToken()));
        try {
            authTable.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        // TODO: Still isn't deleting but IDK why?
        return new LogoutResponse();
    }

    public UserResponse getUser(UserRequest request) {
        if (request.getAlias() == null || request.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }

        return new UserResponse(getUser(request.getAlias()));
    }

    public PostStatusResponse postStatus(PostStatusRequest postStatusRequest) {
        // TODO: Handle the status and stuff
        if (postStatusRequest.getStatus() == null || postStatusRequest.getAuthToken() == null) {
            throw new RuntimeException("Invalid request object");
        }

        // add status to the feed table. That means create a copy of the status for each person that follows him.
        // First we will need to get a list of people who follow him.
//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
//        DynamoDB dynamoDB = new DynamoDB(client);
//        Table table = dynamoDB.getTable("feed");
//        try {
//            PutItemOutcome outcome = table.putItem(new Item()
//                    .withPrimaryKey("user_handle", request.getAlias())
//                    .with("firstName", request.getFirstName())
//                    .with("lastName", request.getLastName())
//                    .with("password", request.getPassword()));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            throw e;
//        }

// post, date, mentions, urls

        // add a status to the story table. That means create a status for this user and add it.
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("story");
        try {
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("user_handle", postStatusRequest.getStatus().getUser().getAlias())
                    .with("post", postStatusRequest.getStatus().getPost())
                    .with("date", postStatusRequest.getStatus().getDate())
                    .with("mentions", postStatusRequest.getStatus().getMentions().toString())
                    .with("urls", postStatusRequest.getStatus().getUrls().toString()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        return new PostStatusResponse();
    }






    private User getUser(String alias) {
        // get the user table
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        // get photo link from s3
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        String fileName = String.format("%s_profile_image", alias);
        URL url = s3.getUrl("ppictures", fileName);

        System.out.println("filename: " + fileName);

        // get User info
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_handle", alias);
        User user;
        try {
            Item outcome = userTable.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            user = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("user_handle"), url.toString());
        } catch (Exception e) {
            System.err.println("Unable to read item");
            throw e;
        }

        return user;
    }

    private AuthToken generateAuthToken(String userAlias) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // Generate Authtoken
        LocalDateTime localDateTime = LocalDateTime.now();
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), localDateTime.toString(), userAlias);
        // Put authtoken in table
        Table authTable = dynamoDB.getTable("auth_table");
        try {
            PutItemOutcome outcome = authTable.putItem(new Item().withPrimaryKey("authtoken", authToken.getToken()).with("time", authToken.getDatetime()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        return authToken;
    }

    private void addUser(RegisterRequest request) {
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


    }

    private URL addUserImage(RegisterRequest request) {
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
        return url;
    }
}

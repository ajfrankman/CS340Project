package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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

    public User getUser(String alias) {
        // get the user table
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        // TODO check Password


        // get User info
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_handle", alias);
        User user;
        try {
            Item outcome = userTable.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            user = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("user_handle"), outcome.getString("imageURL"));
        } catch (Exception e) {
            System.err.println("Unable to read item");
            throw e;
        }

        return user;
    }

    public void addUser(String alias, String firstName, String lastName, String password, String imageURL) {
        // Add user to table
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("users");
        try {
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("user_handle", alias)
                    .with("firstName", firstName)
                    .with("lastName", lastName)
                    .with("password", password)
                    .with("followingCount", 0)
                    .with("followersCount", 0)
                    .with("imageURL", imageURL));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public URL addUserImage(String alias, String image) {
        // Add Image to s3 Bucket
        URL url = null;
        try {
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();

            // Create user profile image file to upload to s3.
            String fileName = String.format("%s_profile_image", alias);

            // Get image bytes.
            byte[] imageBytes = Base64.getDecoder().decode(image);

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

    public void updateFollowingCount(String userAlias, int update) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("users");

//        User user = getUser(userAlias);

        UpdateItemSpec spec = new UpdateItemSpec()
                .withPrimaryKey("user_handle", userAlias)
                .withUpdateExpression("add followingCount :r")
                .withValueMap(new ValueMap().withNumber(":r", update));

        table.updateItem(spec);
    }

    public void updateFollowersCount(String userAlias, int update) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("users");

//        User user = getUser(userAlias);

        UpdateItemSpec spec = new UpdateItemSpec()
                .withPrimaryKey("user_handle", userAlias)
                .withUpdateExpression("add followersCount :r")
                .withValueMap(new ValueMap().withNumber(":r", update));

        table.updateItem(spec);
    }

    public int getUserFollowingCount(String userAlias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_handle", userAlias);

        int followingCount;
        try {
            Item outcome = userTable.getItem(spec);
            followingCount = outcome.getInt("followingCount");
        } catch (Exception e) {
            throw e;
        }
        return followingCount;
    }

    public int getUserFollowersCount(String userAlias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("user_handle", userAlias);

        int followersCount;
        try {
            Item outcome = userTable.getItem(spec);
            followersCount = outcome.getInt("followersCount");
        } catch (Exception e) {
            throw e;
        }
        return followersCount;
    }

    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems("users");

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("user_handle", user.getAlias())
                    .with("firstName", user.getFirstName())
                    .with("lastName", user.getLastName())
                    .with("password", "password")
                    .with("followingCount", 0)
                    .with("followersCount", 0)
                    .with("imageURL", user.getImageUrl());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("users");
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
        }
    }
}

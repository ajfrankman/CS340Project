package edu.byu.cs.tweeter.server.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;

import java.time.LocalDateTime;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.factoryinterfaces.AuthDAOInterface;

public class AuthDAO implements AuthDAOInterface {
    @Override
    public AuthToken generateAuthToken(String userAlias) {

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

    @Override
    public void removeAuthToken(AuthToken authToken) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // Remove authtoken from table
        Table authTable = dynamoDB.getTable("auth_table");
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(new PrimaryKey("authtoken", authToken.getToken()));
        try {
            authTable.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}

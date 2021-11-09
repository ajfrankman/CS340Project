import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;

public class M4Tests {
    FakeData fakeData = new FakeData();
    ServerFacade serverFacade = new ServerFacade();
    @Test
    public void register() {
        RegisterRequest registerRequest = new RegisterRequest("AJ", "Frankman", "@peoncup", "password", "");
        RegisterResponse registerResponse = null;
        try {
            registerResponse = serverFacade.register(registerRequest, "/register");
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
        User user = registerResponse.getRegisteredUser();
        Assertions.assertEquals(fakeData.getFirstUser().getAlias(), user.getAlias());
        Assertions.assertEquals(fakeData.getFirstUser().getFirstName(), user.getFirstName());
        Assertions.assertEquals(fakeData.getFirstUser().getLastName(), user.getLastName());
        Assertions.assertEquals(fakeData.getFirstUser().getImageBytes(), user.getImageBytes());
    }

    @Test
    public void getFollowers() {

        FollowersRequest followersRequest;
        AuthToken authToken = new AuthToken();
        followersRequest = new FollowersRequest(authToken, "@allen", 10, null);
        FollowersResponse followersResponse = null;
        try {
            followersResponse = serverFacade.getFollowers(followersRequest, "/getfollowers");
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(followersResponse.getFollowers().size(), 10);

        List<User> users = fakeData.getFakeUsers();

        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(users.get(i), followersResponse.getFollowers().get(i));
        }

        Assertions.assertTrue(followersResponse.isSuccess());
    }

    @Test
    public void getFollowingCount() {
        AuthToken authToken = new AuthToken();
        GetFollowingCountRequest getFollowingCountRequest = new GetFollowingCountRequest(authToken, fakeData.getFirstUser());

        GetFollowingCountResponse getFollowingCountResponse = null;
        try {
            getFollowingCountResponse = serverFacade.getFollowingCount(getFollowingCountRequest, "getfollowingcount");
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(getFollowingCountResponse.getCount(), 9);
        Assertions.assertTrue(getFollowingCountResponse.isSuccess());

    }
}
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.FakeData;

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
}
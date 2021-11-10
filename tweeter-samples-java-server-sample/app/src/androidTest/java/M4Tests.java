import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.service.paged.PagesObserver;
import edu.byu.cs.tweeter.client.model.service.service.paged.StoryService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoriesResponse;
import edu.byu.cs.tweeter.model.util.FakeData;
import edu.byu.cs.tweeter.model.util.Pair;

public class M4Tests {
    FakeData fakeData = new FakeData();
    ServerFacade serverFacade = new ServerFacade();


    @Before
    public void setup() {
        // Setup valid and invalid requests to be used in the tests
        validRequest = new StoriesRequest(fakeData.getFakeStatuses().get(0), 5);

        // Setup success and failure responses to be used in the tests
        List<Status> success_statuses = Arrays.asList(fakeData.getFakeStatuses().get(0), fakeData.getFakeStatuses().get(1), fakeData.getFakeStatuses().get(2), fakeData.getFakeStatuses().get(3), fakeData.getFakeStatuses().get(4));
        successResponse = new StoriesResponse(success_statuses, true);

        // Setup an observer for the FollowService
//        observer = new StoryServiceObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
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




    private StoriesRequest validRequest;

    private StoriesResponse successResponse;

    private PagesObserver observer;

    private CountDownLatch countDownLatch;





    private class StoryServiceObserver implements PagesObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void getItemsSucceeded(List<Status> items, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = items;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStories() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }



    }


//    private StoryService setupStoryServiceSpy(StoriesResponse serverFacadeResponse) {
//        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
//        try {
//            Mockito.when(mockServerFacade.getStories(Mockito.any(), Mockito.any())).thenReturn(serverFacadeResponse);
//        } catch (Exception e) {
//            // We won't actually get an exception while setting up the mock
//        }
//
//        StoryService storyService = new StoryService();
//        StoryService followServiceSpy = Mockito.spy(storyService);
//        Mockito.when(followServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
//
//        return followServiceSpy;
//    }




    @Test
    public void userStories() throws InterruptedException{

//        StoryService storyService = new StoryService(fakeData.getFirstUser(), 10, fakeData.getFakeStatuses().get(0),mockGetStoryObserver)
        StoryService storyService = new StoryService();
        PagesObserver<Status> mockObserver = (PagesObserver<Status>) Mockito.mock(PagesObserver.class);

        Answer<Void> storyAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };
        Mockito.doAnswer(storyAnswer).when(mockObserver).getItemsSucceeded(Mockito.anyList(), Mockito.anyBoolean());
        Mockito.doAnswer(storyAnswer).when(mockObserver).handleFailure(Mockito.any());

        storyService.getStory(fakeData.getFirstUser(), 10, fakeData.getFakeStatuses().get(0), mockObserver);

        countDownLatch.await();
        resetCountDownLatch();

        Mockito.verify(mockObserver).getItemsSucceeded(Mockito.anyList(), true);

    }
}
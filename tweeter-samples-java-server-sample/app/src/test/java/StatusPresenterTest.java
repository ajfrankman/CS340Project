
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.service.PostStatusService;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.MainView;
import edu.byu.cs.tweeter.client.presenter.viewIntefaces.StatusView;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusPresenterTest {

    private MainView mockMainView;
    private PostStatusService mockPostStatusService;
    private Cache mockCache;
    private Status status;
    private MainActivityPresenter mainActivityPresenterSpy;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainView.class);
        mockPostStatusService = Mockito.mock(PostStatusService.class);
        mockCache = Mockito.mock(Cache.class);

//        String post, User user, String datetime, List<String> urls, List<String> mentions
        User user = new User();
        ArrayList<String> urls = new ArrayList<>();
        urls.add("URL1");
        urls.add("URL2");

        ArrayList<String> mentions = new ArrayList<>();
        mentions.add("mention 1");
        mentions.add("mention 2");

        status = new Status("This is a Post.", user, "dateTimeString", urls, mentions);



        mainActivityPresenterSpy = Mockito.spy(new MainActivityPresenter(mockMainView));
        Mockito.doReturn(mockPostStatusService).when(mainActivityPresenterSpy).getPostStatusService();

        Cache.setInstance(mockCache);
    }


    @Test
    public void testPostStatusSucceeded() {

        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PostStatusService.PostStatusObserver observer = invocation.getArgumentAt(1, PostStatusService.PostStatusObserver.class);
                observer.postStatusSucceeded("Status Post successful!");
                return null;
            }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockPostStatusService).postStatus(Mockito.any(), Mockito.any());

        mainActivityPresenterSpy.postStatus("This is a status String.");

        Mockito.verify(mockMainView).makeToast("Status Post successful!");

    }

    @Test
    public void testPostStatusFailed() {

        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PostStatusService.PostStatusObserver observer = invocation.getArgumentAt(1, PostStatusService.PostStatusObserver.class);
                observer.handleFailure("This isn't good!");
                return null;
            }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockPostStatusService).postStatus(Mockito.any(), Mockito.any());

        // Run the test case
        mainActivityPresenterSpy.postStatus("This is a status String.");

        Mockito.verify(mainActivityPresenterSpy).handleFailure("This isn't good!");

    }

    @Test
    public void testPostStatusValues() {

        Answer<Void> postStatusSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PostStatusService.PostStatusObserver observer = invocation.getArgumentAt(1, PostStatusService.PostStatusObserver.class);
                String status = invocation.getArgumentAt(0, String.class);
                Assertions.assertEquals(status, "This is a status String.");
                return null;
            }
        };

        Mockito.doAnswer(postStatusSucceededAnswer).when(mockPostStatusService).postStatus(Mockito.any(), Mockito.any());

        // Run the test case
        mainActivityPresenterSpy.postStatus("This is a status String.");
    }


}

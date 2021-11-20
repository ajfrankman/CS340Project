package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StoriesRequest;

public class StoriesResponse extends PagedResponse {

    public List<Status> stories;

    public StoriesResponse() {
        super(false, false);
    }

    public StoriesResponse (String message) {
        super(false, message, false);
    }

    public StoriesResponse(List<Status> stories, boolean hasMorePages) {
        super(true, hasMorePages);
        this.stories = stories;
    }

    public List<Status> getStories() { return stories; }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        StoriesResponse that = (StoriesResponse) param;

        return (Objects.equals(stories, that.stories) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stories);
    }

    public void setStories(List<Status> stories) {
        this.stories = stories;
    }
}

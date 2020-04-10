package astric.model.service.response.follow;

import astric.model.domain.User;
import astric.model.service.request.follow.FollowersRequest;
import astric.model.service.response.PagedResponse;

import java.util.List;

/**
 * A paged response for a {@link FollowersRequest}.
 */
public class FollowersResponse extends PagedResponse {
    private List<User> followers;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public FollowersResponse(String message) {
        super(false, message, false);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param followees the followees to be included in the result.
     * @param hasMorePages an indicator or whether more data is available for the request.
     */
    public FollowersResponse(List<User> followees, boolean hasMorePages) {
        super(true, hasMorePages);
        this.followers = followees;
    }

    /**
     * Returns the followees for the corresponding request.
     *
     * @return the followees.
     */
    public List<User> getFollowers() {
        return followers;
    }
}

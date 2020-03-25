package astric.model.service.request.follow;

import astric.model.domain.User;

public class FollowingRequest {
    private int limit;
    private String followerUsername;
    private User lastFollowee;
    private String authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowingRequest() {}

    /**
     * Creates an instance.
     *
     * @param followerUsername the username of the User whose followees are to be returned.
     * @param limit the maximum number of followees to return.
     * @param lastFollowee the last followee that was returned in the previous request (null if
     *                     there was no previous request or if no followees were returned in the
     *                     previous request).
     */
    public FollowingRequest(String followerUsername, int limit, User lastFollowee, String authToken) {
        this.followerUsername = followerUsername;
        this.limit = limit;
        this.lastFollowee = lastFollowee;
        this.authToken = authToken;
    }


    /**
     * Returns the follower whose followees are to be returned by this request.
     *
     * @return the follower.
     */
    public String getFollowerUsername() {
        return followerUsername;
    }

    /**
     * Sets the follower.
     *
     * @param followerUsername the follower's username.
     */
    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    /**
     * Returns the number representing the maximum number of followees to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the limit.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the last followee that was returned in the previous request or null if there was no
     * previous request or if no followees were returned in the previous request.
     *
     * @return the last followee.
     */
    public User getLastFollowee() {
        return lastFollowee;
    }

    /**
     * Sets the last followee.
     *
     * @param lastFollowee the last followee.
     */
    public void setLastFollowee(User lastFollowee) {
        this.lastFollowee = lastFollowee;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

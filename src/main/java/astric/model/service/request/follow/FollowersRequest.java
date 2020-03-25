package astric.model.service.request.follow;

import astric.model.domain.User;

public class FollowersRequest {
    private int limit;
    private String followeeUsername;
    private User lastFollower;
    private String authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowersRequest() {}

    /**
     * Creates an instance.
     *
     * @param followeeUsername the username of the User whose followers are to be returned.
     * @param limit the maximum number of followers to return.
     * @param lastFollower the last follower that was returned in the previous request (null if
     *                     there was no previous request or if no followers were returned in the
     *                     previous request).
     */
    public FollowersRequest(String followeeUsername, int limit, User lastFollower, String authToken) {
        this.followeeUsername = followeeUsername;
        this.limit = limit;
        this.lastFollower = lastFollower;
        this.authToken = authToken;
    }


    /**
     * Returns the followee whose followers are to be returned by this request.
     *
     * @return the follower.
     */
    public String getFolloweeUsername() {
        return followeeUsername;
    }

    /**
     * Sets the follower.
     *
     * @param followeeUsername the follower's username.
     */
    public void setFolloweeUsername(String followeeUsername) {
        this.followeeUsername = followeeUsername;
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
    public User getLastFollower() {
        return lastFollower;
    }

    /**
     * Sets the last followee.
     *
     * @param lastFollower the last followee.
     */
    public void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

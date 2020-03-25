package astric.model.service.request.follow;

public class FollowRequest {
    private boolean follow; // false if the request is to unfollow
    private String followerUsername;
    private String followeeUsername;
    private String authToken;

    private FollowRequest(){}

    public FollowRequest(boolean follow, String followerUsername, String followeeUsername, String authToken) {
        this.follow = follow;
        this.followerUsername = followerUsername;
        this.followeeUsername = followeeUsername;
        this.authToken = authToken;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public String getFollowerUsername() {
        return followerUsername;
    }

    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    public String getFolloweeUsername() {
        return followeeUsername;
    }

    public void setFolloweeUsername(String followeeUsername) {
        this.followeeUsername = followeeUsername;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

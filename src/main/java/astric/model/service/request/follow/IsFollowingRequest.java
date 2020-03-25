package astric.model.service.request.follow;

public class IsFollowingRequest {
    private String followerUsername;
    private String followeeUsername;
    private String authToken;

    private IsFollowingRequest(){}

    public IsFollowingRequest(String followerUsername, String followeeUsername, String authToken) {
        this.followerUsername = followerUsername;
        this.followeeUsername = followeeUsername;
        this.authToken = authToken;
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

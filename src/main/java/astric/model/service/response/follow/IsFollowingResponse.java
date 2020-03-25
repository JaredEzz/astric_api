package astric.model.service.response.follow;

import astric.model.service.response.Response;

public class IsFollowingResponse extends Response {
    private boolean isFollowing;

    public IsFollowingResponse(boolean success, boolean isFollowing) {
        super(success);
        this.isFollowing = isFollowing;
    }

    public IsFollowingResponse(boolean success, boolean isFollowing, String message) {
        super(success, message);
        this.isFollowing = isFollowing;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}

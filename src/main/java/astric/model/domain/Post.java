package astric.model.domain;

import java.util.Date;

public class Post {
    private User originatingUser;
    private Date timestamp;

    public Post(User originatingUser, Date timestamp, String message) {
        this.originatingUser = originatingUser;
        this.timestamp = timestamp;
        this.message = message;
    }

    private String message;

}

package astric.model.domain;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class Post {
    private User originatingUser;
    private String timestamp;
    private String message;

    Post(){}

    public Post(User originatingUser, String timestamp, String message) {
        this.originatingUser = originatingUser;
        this.timestamp = timestamp;
        this.message = message;
    }

    public User getOriginatingUser() {
        return originatingUser;
    }

    public void setOriginatingUser(User originatingUser) {
        this.originatingUser = originatingUser;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

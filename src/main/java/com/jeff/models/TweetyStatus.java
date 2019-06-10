package com.jeff.models;

import java.util.Date;

public class TweetyStatus {
    private String id;
    private String message;
    private TweetyUser user;
    private Date createdAt;

    public TweetyStatus(String i, String m, TweetyUser u, Date c) {
        id = i;
        message = m;
        user = u;
        createdAt = c;
    }

    public String getId() { return id; }
    public String getMessage() { return message; }
    public TweetyUser getUser() { return user; }
    public Date getCreatedAt() { return createdAt; }
}

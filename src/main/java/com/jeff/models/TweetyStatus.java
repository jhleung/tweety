package com.jeff.models;

import java.util.Date;

public class TweetyStatus {
    private String id;
    private String message;
    private String handle;
    private String name;
    private String profileImageUrl;
    private Date createdAt;

    public TweetyStatus(String i, String m, String h, String n, String p, Date c) {
        id = i;
        message = m;
        handle = h;
        name = n;
        profileImageUrl = p;
        createdAt = c;
    }

    public String getId() { return id; }
    public String getMessage() { return message; }
    public String getHandle() { return handle; }
    public String getName() { return name; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public Date getCreatedAt() { return createdAt; }
}

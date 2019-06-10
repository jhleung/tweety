package com.jeff.models;

public class TweetyUser {
    private String handle;
    private String name;
    private String profileImageUrl;

    public TweetyUser(String h, String n, String p) {
        handle = h;
        name = n;
        profileImageUrl = p;
    }

    public String getHandle() { return handle; }
    public String getName() { return name; }
    public String getProfileImageUrl() { return profileImageUrl; }
}

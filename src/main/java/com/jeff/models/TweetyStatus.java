package com.jeff.models;

import java.util.Date;

public class TweetyStatus {
    private String message;
    private String handle;
    private String name;
    private String profileImageUrl;
    private Date createdAt;

    private TweetyStatus(TweetyBuilder tb) {
        message = tb.message;
        handle = tb.handle;
        name = tb.name;
        profileImageUrl = tb.profileImageUrl;
        createdAt = tb.createdAt;
    }

    public String getMessage() { return message; }
    public String getHandle() { return handle; }
    public String getName() { return name; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public Date getCreatedAt() { return createdAt; }

    public static class TweetyBuilder {
        private String message;
        private String handle;
        private String name;
        private String profileImageUrl;
        private Date createdAt;

        public TweetyBuilder() { }

        public TweetyBuilder message(String m) {
            message = m;
            return this;
        }
        public TweetyBuilder handle(String h) {
            handle = h;
            return this;
        }
        public TweetyBuilder name(String n) {
            name = n;
            return this;
        }
        public TweetyBuilder profileImageUrl(String p) {
            profileImageUrl = p;
            return this;
        }
        public TweetyBuilder createdAt(Date c) {
            createdAt = c;
            return this;
        }

        public TweetyStatus build() {
            return new TweetyStatus(this);
        }
    }
}

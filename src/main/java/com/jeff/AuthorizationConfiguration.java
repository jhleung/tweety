package com.jeff;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AuthorizationConfiguration {
    @NotNull
    private boolean debug;
    @NotEmpty
    private String oAuthConsumerKey;
    @NotEmpty
    private String oAuthConsumerSecret;
    @NotEmpty
    private String oAuthAccessToken;
    @NotEmpty
    private String oAuthAccessTokenSecret;

    @JsonProperty
    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean d) {
        debug = d;
    }

    @JsonProperty
    public String getOAuthConsumerKey() {
        return oAuthConsumerKey;
    }

    public void setOAuthConsumerKey(String key) {
        oAuthConsumerKey = key;
    }

    @JsonProperty
    public String getOAuthConsumerSecret() {
        return oAuthConsumerSecret;
    }

    public void setOAuthConsumerSecret(String secret) {
        oAuthConsumerSecret = secret;
    }

    @JsonProperty
    public String getOAuthAccessToken() {
        return oAuthAccessToken;
    }

    public void setOAuthAccessToken(String accessToken) {
        oAuthAccessToken = accessToken;
    }

    @JsonProperty
    public String getOAuthAccessTokenSecret() {
        return oAuthAccessTokenSecret;
    }

    public void setOAuthAccessTokenSecret(String secret) {
        oAuthAccessTokenSecret = secret;
    }

}

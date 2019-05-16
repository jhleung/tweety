package com.jeff;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TweetyConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private TwitterAuthorizationConfiguration twitterAuthorization = new TwitterAuthorizationConfiguration();

    public TwitterAuthorizationConfiguration getTwitter4jAuthorizationConfiguration() {
        return twitterAuthorization;
    }
}

package com.jeff;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TweetyConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private Twitter4jAuthorizationConfiguration twitter4jAuthorization = new Twitter4jAuthorizationConfiguration();

    public Twitter4jAuthorizationConfiguration getTwitter4jAuthorizationConfiguration() {
        return twitter4jAuthorization;
    }
}

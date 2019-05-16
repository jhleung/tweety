package com.jeff;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TweetyConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private AuthorizationConfiguration authorization = new AuthorizationConfiguration();

    public AuthorizationConfiguration getTwitter4jAuthorizationConfiguration() {
        return authorization;
    }
}

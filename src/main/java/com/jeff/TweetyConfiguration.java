package com.jeff;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class TweetyConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private Twitter4jConfiguration twitter4j = new Twitter4jConfiguration();

    public Twitter4jConfiguration getTwitter4jConfiguration() {
        return twitter4j;
    }
}

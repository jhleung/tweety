package com.jeff;

import com.jeff.resources.TweetyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetyApplication extends Application<TweetyConfiguration> {
    public static void main(String [] args) throws Exception{
       new TweetyApplication().run(args);
    }

    @Override
    public void run(TweetyConfiguration configuration, Environment environment) {
        Twitter4jAuthorizationConfiguration Twitter4jAuthorizationConfiguration = configuration.getTwitter4jAuthorizationConfiguration();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(Twitter4jAuthorizationConfiguration.getDebug())
                .setOAuthConsumerKey(Twitter4jAuthorizationConfiguration.getOAuthConsumerKey())
                .setOAuthConsumerSecret(Twitter4jAuthorizationConfiguration.getOAuthConsumerSecret())
                .setOAuthAccessToken(Twitter4jAuthorizationConfiguration.getOAuthAccessToken())
                .setOAuthAccessTokenSecret(Twitter4jAuthorizationConfiguration.getOAuthAccessTokenSecret());
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        TweetyResource tweetyResource = new TweetyResource(twitter);
        environment.jersey().register(tweetyResource);
    }
}
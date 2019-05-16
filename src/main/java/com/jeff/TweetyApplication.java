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
        TwitterAuthorizationConfiguration TwitterAuthorizationConfiguration = configuration.getTwitter4jAuthorizationConfiguration();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(TwitterAuthorizationConfiguration.getDebug())
                .setOAuthConsumerKey(TwitterAuthorizationConfiguration.getOAuthConsumerKey())
                .setOAuthConsumerSecret(TwitterAuthorizationConfiguration.getOAuthConsumerSecret())
                .setOAuthAccessToken(TwitterAuthorizationConfiguration.getOAuthAccessToken())
                .setOAuthAccessTokenSecret(TwitterAuthorizationConfiguration.getOAuthAccessTokenSecret());
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        TweetyResource tweetyResource = new TweetyResource(twitter);
        environment.jersey().register(tweetyResource);
    }
}
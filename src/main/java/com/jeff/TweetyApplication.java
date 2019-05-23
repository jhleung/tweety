package com.jeff;

import com.jeff.resources.TweetyResource;
import com.jeff.services.TweetyService;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetyApplication extends Application<TweetyConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(TweetyApplication.class);

    public static void main(String [] args) throws Exception{
        new TweetyApplication().run(args);
    }

    @Override
    public void run(TweetyConfiguration configuration, Environment environment) {
        logger.info("Tweety application starting up...");
        TwitterAuthorizationConfiguration TwitterAuthorizationConfiguration = configuration.getTwitter4jAuthorizationConfiguration();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(TwitterAuthorizationConfiguration.getDebug())
                .setOAuthConsumerKey(TwitterAuthorizationConfiguration.getOAuthConsumerKey())
                .setOAuthConsumerSecret(TwitterAuthorizationConfiguration.getOAuthConsumerSecret())
                .setOAuthAccessToken(TwitterAuthorizationConfiguration.getOAuthAccessToken())
                .setOAuthAccessTokenSecret(TwitterAuthorizationConfiguration.getOAuthAccessTokenSecret());

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        TweetyService tweetyService = TweetyService.getInstance(twitter);
        TweetyResource tweetyResource = new TweetyResource(tweetyService);

        environment.jersey().register(tweetyResource);
        logger.info("Tweety application started successfully.");
    }
}
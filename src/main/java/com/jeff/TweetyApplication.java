package com.jeff;

import com.jeff.resources.TweetyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TweetyApplication extends Application<TweetyConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(TweetyApplication.class);

    public static void main(String [] args) throws Exception{
        new TweetyApplication().run(args);
    }

    @Override
    public void run(TweetyConfiguration configuration, Environment environment) {
        logger.info("Tweety application starting up...");
        TweetyModule twitter = new TweetyModule(configuration);
        TweetyComponent component = DaggerTweetyComponent.builder()
                .tweetyModule(twitter)
                .build();
        TweetyResource tweetyResource = component.getTweetyResource();
        environment.jersey().register(tweetyResource);
        logger.info("Tweety application started successfully.");
    }
}
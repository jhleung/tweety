package com.jeff;

import com.jeff.resources.TweetyResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TweetyApplication extends Application<Configuration> {
    public static void main(String [] args) throws Exception{
       new TweetyApplication().run(args);
    }

    @Override
    public void run(Configuration configuration,  Environment environment) {
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        TweetyResource tweetyResource = new TweetyResource(twitter);
        environment.jersey().register(tweetyResource);
    }
}
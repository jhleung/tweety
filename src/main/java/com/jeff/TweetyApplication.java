package com.jeff;

import com.jeff.resources.TweetyResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterRegistration;

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

        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        environment.jersey().register(tweetyResource);
        logger.info("Tweety application started successfully.");
    }
}
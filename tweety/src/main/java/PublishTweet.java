package main.java;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.Properties;

public class PublishTweet {
    private static final Logger logger = Logger.getLogger(PublishTweet.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.severe("No tweet was given to publish.");
            return;
        } else if (args.length > 1) {
            logger.warning("Only one tweet may be published at a time. Proceeding to publish the first tweet given.");
        }
        publish(args[0]);
    }

    public static void publish(String status) {
        try {
            Configuration conf = getConfiguration();
            TwitterFactory factory = new TwitterFactory(conf);
            Twitter twitter = factory.getInstance();
            try {
                twitter.updateStatus(status);
            } catch (TwitterException e) {
                logger.severe(e.getMessage());
            }
        } catch(IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static Configuration getConfiguration() throws IOException {
        Properties prop = new Properties();
        prop.load(PublishTweet.class.getResourceAsStream("resources/config.properties"));
        String consumerKey = prop.getProperty("consumer.key");
        String consumerSecret = prop.getProperty("consumer.secret");
        String accessToken = prop.getProperty("access.token");
        String accessTokenSecret = prop.getProperty("access.token.secret");

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        return cb.build();
    }

}
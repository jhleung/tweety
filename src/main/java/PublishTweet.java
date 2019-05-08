import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;

import java.util.logging.Logger;

import java.io.IOException;

public class PublishTweet {
    private static final int MAX_TWEET_LENGTH = 280;

    private static final Logger logger = Logger.getLogger(PublishTweet.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.severe("No tweet was given to publish.");
            return;
        } else if (args.length > 1) {
            logger.warning("Only one tweet may be published at a time. Proceeding to publish the first tweet given.");
        }
        String status = args[0];

        if (!validateLength(status)) {
            logger.severe("Tweet may not exceed " + MAX_TWEET_LENGTH + " characters");
            return;
        }

        publish(status);
    }

    public static void publish(String status) {
        try {
            Configuration conf = TwitterHelper.getConfiguration();
            TwitterFactory factory = new TwitterFactory(conf);
            Twitter twitter = factory.getInstance();
            try {
                twitter.updateStatus(status);
                logger.info("Success! Status published with message: " + status);
            } catch (TwitterException e) {
                logger.severe(e.getMessage());
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static boolean validateLength(String status) {
        return status.length() <= MAX_TWEET_LENGTH;
    }

}

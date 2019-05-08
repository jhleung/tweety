import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.logging.Logger;


public class PublishTweet {
    private static final int MAX_TWEET_LENGTH = 280;

    private static final Logger logger = Logger.getLogger(PublishTweet.class.getName());

    public static void publish(String status) {
        if (!validateLength(status)) {
            logger.severe("Tweet may not exceed " + MAX_TWEET_LENGTH + " characters");
            return;
        }
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        try {
            twitter.updateStatus(status);
            logger.info("Success! Status published with message: " + status);
        } catch (TwitterException e) {
            logger.severe(e.getMessage());
        }
    }

    private static boolean validateLength(String status) {
        return status.length() <= MAX_TWEET_LENGTH;
    }

}
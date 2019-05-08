import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class PullTweets {
    private static final Logger logger = Logger.getLogger(PullTweets.class.getName());

    public static void pullHomeTimeline() {
        try {
            Configuration conf = TwitterHelper.getConfiguration();
            TwitterFactory factory = new TwitterFactory(conf);
            Twitter twitter = factory.getInstance();

            try {
                List<Status> statuses = twitter.getHomeTimeline();
                for (Status status : statuses) {
                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                }
            } catch (TwitterException e) {
                logger.severe(e.getMessage());
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }
}
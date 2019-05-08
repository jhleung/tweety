import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.List;
import java.util.logging.Logger;

public class PullTweets {
    private static final Logger logger = Logger.getLogger(PullTweets.class.getName());

    public static void pullHomeTimeline() {
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();

        try {
            List<Status> statuses = twitter.getHomeTimeline();
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }
        } catch (TwitterException e) {
            logger.severe(e.getMessage());
        }
    }
}
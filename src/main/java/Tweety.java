import java.util.logging.Logger;

public class Tweety {
    private static final Logger logger = Logger.getLogger(Tweety.class.getName());

    public static void main(String [] args) {
        if (args.length == 0) {
            PullTweets.pullHomeTimeline();
        } else {
            if (args.length > 1) {
                logger.warning("Only one tweet may be published at a time. Proceeding to publish the first tweet given.");
            }
            PublishTweet.publish(args[0]);
        }
    }
}
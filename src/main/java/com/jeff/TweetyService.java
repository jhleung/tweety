package com.jeff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.List;

public class TweetyService {
    private static TweetyService tweetyServiceInstance;

    private final Twitter twitter;

    private static final Logger logger = LoggerFactory.getLogger(TweetyService.class);

    private TweetyService(Twitter t) {
        twitter = t;
    }

    public static TweetyService getInstance(Twitter t) {
        if (tweetyServiceInstance == null) {
            tweetyServiceInstance = new TweetyService(t);
        }
        return tweetyServiceInstance;
    }

    public Status publishTweet(String message) throws IOException {
        logger.debug("Message to be published: \"{}\"", message);

        if (!validateLength(message)) {
            throw new IOException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
        } else {
            try {
                Status status = twitter.updateStatus(message);
                logger.info("Message \"{}\" published successfully", message);
                return status;
            } catch (TwitterException e) {
                logger.error("Message \"{}\" was not published successfully.", message, e.getErrorMessage(), e);
                if (message.isEmpty()) {
                    throw new IOException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
                } else if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    throw new IOException(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
                } else {
                    throw new IOException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
                }
            }
        }
    }

    public List<Status> pullTweets() throws IOException {
        try {
            List<Status> statuses =  twitter.getHomeTimeline();
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the timeline was pulled.");
            return statuses;
        } catch (TwitterException e) {
            logger.error("Timeline was not pulled successfully. {}", e.getErrorMessage(), e);
            throw new IOException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

package com.jeff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

public class TweetyService {
    private static TweetyService tweetyServiceInstance;

    private final Twitter twitter;

    private static final String PUBLISH_TWEET_ERROR_MSG = "Message \"{}\" was not published successfully.";
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

    public Response publishTweet(String message) {
        logger.debug("Message to be published: \"{}\"", message);
        Response.ResponseBuilder rb = Response.status(Response.Status.OK);

        if (!validateLength(message)) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
        } else {
            try {
                rb.entity(twitter.updateStatus(message));
                logger.info("Message \"{}\" published successfully", message);
            } catch (TwitterException e) {
                rb.status(Response.Status.INTERNAL_SERVER_ERROR);
                logger.error(PUBLISH_TWEET_ERROR_MSG, message, e.getErrorMessage(), e);
                if (message.isEmpty()) {
                    rb.entity(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
                } else if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    rb.entity(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
                } else {
                    rb.entity(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
                }
            }
        }
        return rb.build();
    }

    public Response pullTweets() {
        Response.ResponseBuilder rb = Response.status(Response.Status.OK);

        try {
            rb.entity(twitter.getHomeTimeline());
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the timeline was pulled.");
        } catch (TwitterException e) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
            logger.error("Timeline was not pulled successfully. {}", e.getErrorMessage(), e);
        }
        return rb.build();
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

package com.jeff;

import com.jeff.models.TweetyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


import java.util.ArrayList;
import java.util.List;

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

    public TweetyStatus publishTweet(String message) throws TweetyException {
        logger.debug("Message to be published: \"{}\"", message);

        if (!validateLength(message)) {
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
            throw new TweetyException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
        } else if (message.isEmpty()) {
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
            throw new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
        } else {
            try {
                TweetyStatus tweetyStatus = createTweetyStatus(twitter.updateStatus(message));
                logger.info("Message \"{}\" published successfully", message);
                return tweetyStatus;
            } catch (TwitterException e) {
                logger.error(PUBLISH_TWEET_ERROR_MSG, message, e.getErrorMessage(), e);
                 if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    throw new TweetyException(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
                } else {
                    throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
                }
            }
        }
    }

    public List<TweetyStatus> pullTweets() throws TweetyException {
        try {
            List<TweetyStatus> tweetyStatuses = new ArrayList<>();
            twitter.getHomeTimeline().forEach(s -> tweetyStatuses.add(createTweetyStatus(s)));
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the timeline was pulled.");
            return tweetyStatuses;
        } catch (TwitterException e) {
            logger.error("Timeline was not pulled successfully. {}", e.getErrorMessage(), e);
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    private TweetyStatus createTweetyStatus(Status s) {
        return new TweetyStatus.TweetyBuilder()
                .message(s.getText())
                .handle(s.getUser().getScreenName())
                .name(s.getUser().getName())
                .profileImageUrl(s.getUser().getProfileImageURLHttps())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

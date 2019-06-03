package com.jeff.services;

import com.jeff.TweetyCache;
import com.jeff.TweetyConstantsRepository;
import com.jeff.TweetyException;
import com.jeff.models.TweetyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class TweetyService {
    private String lastPublishedTweet, publishedTweetSincePull, publishedTweetSinceFilter;

    private final Twitter twitter;
    private final TweetyCache cache;

    private static final String PULL_TWEETS_KEY = "HOME_TIMELINE";
    private static final String FILTER_TWEETS_KEY_PREFIX = "FILTERED_TIMELINE";
    private static final String TWEET_SINCE_FILTER_KEY_PREFIX = "LAST_TWEET_SINCE_FILTER";
    private static final String PUBLISH_TWEET_ERROR_MSG = "Message \"{}\" was not published successfully.";
    private static final Logger logger = LoggerFactory.getLogger(TweetyService.class);

    @Inject
    public TweetyService(Twitter t) {
        cache = new TweetyCache(TimeUnit.DAYS.toMillis(1));
        twitter = t;
    }

    public TweetyStatus publishTweet(String message) throws TweetyException {
        logger.debug("Message to be published: \"{}\"", message);
        if (cache.contains(message)) {
            throw (TweetyException) cache.get(message);
        }

        if (!validateLength(message)) {
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
            cache.put(message, exception);
            throw exception;
        } else if (message.isEmpty()) {
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
            cache.put(message, exception);
            throw exception;
        } else {
            try {
                return Stream.of(twitter.updateStatus(message)).map(s -> {
                    logger.info("Message \"{}\" published successfully", message);
                    TweetyStatus ts = new TweetyStatus(s.getText(), s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps(), s.getCreatedAt());
                    lastPublishedTweet = message;
                    return ts;
                }).findFirst().get();
            } catch (TwitterException e) {
                logger.error(PUBLISH_TWEET_ERROR_MSG, message, e.getMessage(), e);
                if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    throw new TweetyException(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
                }
                throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
            }
        }
    }

    public List<TweetyStatus> pullTweets() throws TweetyException {
        if (publishedTweetSincePull == lastPublishedTweet && cache.contains(PULL_TWEETS_KEY)) {
            return (List<TweetyStatus>) cache.get(PULL_TWEETS_KEY);
        }

        publishedTweetSincePull = lastPublishedTweet;
        try {
            final List<TweetyStatus> tweetyStatuses = twitter.getHomeTimeline().stream()
                    .map(s -> new TweetyStatus(s.getText(), s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps(), s.getCreatedAt()))
                    .collect(Collectors.toList());
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the timeline was pulled.");
            cache.put(PULL_TWEETS_KEY, tweetyStatuses);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("Timeline was not pulled successfully. {}", e.getMessage(), e);
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    public List<TweetyStatus> filterTweets(String keyword) throws TweetyException {
        if (keyword.isEmpty())
            throw new TweetyException(TweetyConstantsRepository.EMPTY_KEYWORD_ERROR_MSG);

        String key = FILTER_TWEETS_KEY_PREFIX + keyword;
        String lastTweetSinceFilterKey = TWEET_SINCE_FILTER_KEY_PREFIX + keyword;
        if (cache.contains(lastTweetSinceFilterKey) && cache.get(lastTweetSinceFilterKey) == lastPublishedTweet && cache.contains(key)) {
            return (List<TweetyStatus>) cache.get(key);
        }

        cache.put(lastTweetSinceFilterKey, lastPublishedTweet);
        try {
            final List<TweetyStatus> tweetyStatuses = twitter.getHomeTimeline().stream()
                    .filter(s -> s.getText().contains(keyword))
                    .map(s -> new TweetyStatus(s.getText(), s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps(), s.getCreatedAt()))
                    .collect(Collectors.toList());
            logger.info("Filtered tweets were pulled successfully.");
            if (tweetyStatuses.isEmpty()) {
                logger.info("No tweets containing keyword were found.");
            }
            cache.put(key, tweetyStatuses);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("Filtered tweets were not pulled successfully. {}", e.getMessage(), e);
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

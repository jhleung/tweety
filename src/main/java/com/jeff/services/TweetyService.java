package com.jeff.services;

import com.jeff.TweetyCache;
import com.jeff.TweetyConstantsRepository;
import com.jeff.TweetyException;
import com.jeff.models.TweetyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TweetyService {
    private String lastPublishedTweet, publishedTweetSincePull, publishedTweetSinceFilter;

    private static TweetyService tweetyServiceInstance;

    private final Twitter twitter;
    private final TweetyCache cache;

    private static final String PULL_TWEETS_CACHE_KEY = "HOME_TIMELINE";
    private static final String FILTER_TWEETS_CACHE_KEY = "FILTERED_TIMELINE";
    private static final String PUBLISH_TWEET_ERROR_MSG = "Message \"{}\" was not published successfully.";
    private static final Logger logger = LoggerFactory.getLogger(TweetyService.class);

    private TweetyService(Twitter t) {
        cache = new TweetyCache(TimeUnit.DAYS.toMillis(1));
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
        if (cache.contains(message)) {
            throwIfInstanceOfException(cache.get(message));
        }

        return updateStatus(message);
    }

    public TweetyStatus updateStatus(String message) throws TweetyException {
        if (!validateLength(message)) {
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
            throw new TweetyException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
        } else if (message.isEmpty()) {
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
            throw new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
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
                String errorMessage;
                errorMessage = e.getErrorMessage().equals("Status is a duplicate.") ? TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG
                                    : TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG;
                TweetyException exception = new TweetyException(errorMessage);
                cache.put(message, exception);
                throw exception;
            }
        }
    }

    public List<TweetyStatus> pullTweets() throws TweetyException {
        if (publishedTweetSincePull == lastPublishedTweet && cache.contains(PULL_TWEETS_CACHE_KEY)) {
            Object value = cache.get(PULL_TWEETS_CACHE_KEY);
            throwIfInstanceOfException(value);
            return (List<TweetyStatus>) value;
        }

        return pullHomeTimeline();
    }

    public List<TweetyStatus> pullHomeTimeline() throws TweetyException {
        publishedTweetSincePull = lastPublishedTweet;
        try {
            final List<TweetyStatus> tweetyStatuses = twitter.getHomeTimeline().stream()
                    .map(s -> new TweetyStatus(s.getText(), s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps(), s.getCreatedAt()))
                    .collect(Collectors.toList());
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the timeline was pulled.");
            cache.put(PULL_TWEETS_CACHE_KEY, tweetyStatuses);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("Timeline was not pulled successfully. {}", e.getMessage(), e);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
            cache.put(PULL_TWEETS_CACHE_KEY, exception);
            throw exception;
        }
    }

    public List<TweetyStatus> filterTweets(String keyword) throws TweetyException {
        if (publishedTweetSinceFilter == lastPublishedTweet && cache.contains(FILTER_TWEETS_CACHE_KEY)) {
            Object value = cache.get(FILTER_TWEETS_CACHE_KEY);
            throwIfInstanceOfException(value);
            return (List<TweetyStatus>) value;
        }

        return filterHomeTimeline(keyword);
    }

    public List<TweetyStatus> filterHomeTimeline(String keyword) throws TweetyException {
        publishedTweetSinceFilter = lastPublishedTweet;
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
            cache.put(FILTER_TWEETS_CACHE_KEY, tweetyStatuses);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("Filtered tweets were not pulled successfully. {}", e.getMessage(), e);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
            cache.put(FILTER_TWEETS_CACHE_KEY, exception);
            throw exception;
        }
    }

    private void throwIfInstanceOfException(Object value) throws TweetyException {
        if (value instanceof TweetyException) {
            throw (TweetyException) value;
        }
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

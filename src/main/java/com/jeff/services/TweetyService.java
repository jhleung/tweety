package com.jeff.services;

import com.jeff.TweetyCache;
import com.jeff.TweetyConstantsRepository;
import com.jeff.TweetyException;
import com.jeff.models.TweetyStatus;
import com.jeff.models.TweetyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class TweetyService {

    private final Twitter twitter;
    private final TweetyCache cache;

    private static final String PULL_HOME_TIMELINE_KEY = "HOME_TIMELINE";
    private static final String PULL_USER_TIMELINE_KEY = "USER_TIMELINE";
    private static final String FILTER_TWEETS_KEY = "FILTERED_TIMELINE";
    private static final String STATUS_UPDATE_ERROR_MSG = "Status \"{}\" was not updated successfully.";
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

        validateMaxLength(message);
        if (message.isEmpty()) {
            logger.error(STATUS_UPDATE_ERROR_MSG, message);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
            cache.put(message, exception);
            throw exception;
        }
        StatusUpdate statusUpdate = new StatusUpdate(message);

        return Stream.of(updateStatus(statusUpdate)).map(s -> {
            logger.info("Message \"{}\" published successfully", message);
            return s;
        }).findFirst().get();
    }

    public TweetyStatus replyTweet(String message, String inReplyToId) throws TweetyException {
        logger.debug("Message to be published in response to tweet id: \"{}\" : \"{}\" ", inReplyToId, message);
        if (cache.contains(message + " " + inReplyToId)) {
            throw (TweetyException) cache.get(message + " " + inReplyToId);
        }

        validateMaxLength(message);
        if (message.isEmpty()) {
            logger.error(STATUS_UPDATE_ERROR_MSG, message);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
            cache.put(message + " " + inReplyToId, exception);
            throw exception;
        }

        if (inReplyToId.isEmpty()) {
            logger.error(STATUS_UPDATE_ERROR_MSG, inReplyToId);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.EMPTY_TWEET_ID_ERROR_MSG);
            cache.put(message + " " + inReplyToId, exception);
            throw exception;
        }

        StatusUpdate statusUpdate = new StatusUpdate(message);
        statusUpdate.setInReplyToStatusId(Long.parseLong(inReplyToId));
        
        return Stream.of(updateStatus(statusUpdate)).map(s -> {
            logger.info("Message \"{}\" published successfully in response to tweet id: \"{}\"", message, inReplyToId);
            return s;
        }).findFirst().get();
    }


    public List<TweetyStatus> pullHomeTimeline() throws TweetyException {
        if (cache.contains(PULL_HOME_TIMELINE_KEY)) {
            return (List<TweetyStatus>) cache.get(PULL_HOME_TIMELINE_KEY);
        }

        try {
            final List<TweetyStatus> tweetyStatuses = twitter.getHomeTimeline().stream()
                    .map(s -> new TweetyStatus(String.valueOf(s.getId()), s.getText(), new TweetyUser(s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps()), s.getCreatedAt()))
                    .collect(Collectors.toList());
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the home timeline was pulled.");
            cache.put(PULL_HOME_TIMELINE_KEY, tweetyStatuses);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("Home timeline was not pulled successfully. {}", e.getMessage(), e);
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }


    public List<TweetyStatus> pullUserTimeline() throws TweetyException {
        if (cache.contains(PULL_USER_TIMELINE_KEY)) {
            return (List<TweetyStatus>) cache.get(PULL_USER_TIMELINE_KEY);
        }

        try {
            final List<TweetyStatus> tweetyStatuses = twitter.getUserTimeline().stream()
                    .map(s -> new TweetyStatus(String.valueOf(s.getId()), s.getText(), new TweetyUser(s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps()), s.getCreatedAt()))
                    .collect(Collectors.toList());
            logger.info("User timeline pulled successfully. See log timestamp to see what date the user timeline was pulled.");
            cache.put(PULL_USER_TIMELINE_KEY, tweetyStatuses);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("User timeline was not pulled successfully. {}", e.getMessage(), e);
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    public List<TweetyStatus> filterTweets(String keyword) throws TweetyException {
        if (keyword.isEmpty())
            throw new TweetyException(TweetyConstantsRepository.EMPTY_KEYWORD_ERROR_MSG);

        if (cache.contains(FILTER_TWEETS_KEY)) {
            HashMap<String, List<TweetyStatus>> map = (HashMap) cache.get(FILTER_TWEETS_KEY);
            if (map.containsKey(keyword))
                return map.get(keyword);
        }

        try {
            final List<TweetyStatus> tweetyStatuses = twitter.getHomeTimeline().stream()
                    .filter(s -> s.getText().contains(keyword))
                    .map(s -> new TweetyStatus(String.valueOf(s.getId()), s.getText(), new TweetyUser(s.getUser().getScreenName(),
                            s.getUser().getName(), s.getUser().getProfileImageURLHttps()), s.getCreatedAt()))
                    .collect(Collectors.toList());
            logger.info("Filtered tweets were pulled successfully.");
            if (tweetyStatuses.isEmpty()) {
                logger.info("No tweets containing keyword were found.");
            }
            HashMap<String, List<TweetyStatus>> map = (HashMap) cache.getOrDefault(FILTER_TWEETS_KEY, new HashMap<String, List<TweetyStatus>>());
            map.put(keyword, tweetyStatuses);
            cache.put(FILTER_TWEETS_KEY,  map);
            return tweetyStatuses;
        } catch (TwitterException | NullPointerException e) {
            logger.error("Filtered tweets were not pulled successfully. {}", e.getMessage(), e);
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    private TweetyStatus updateStatus(StatusUpdate statusUpdate) throws TweetyException {
        String message = statusUpdate.getStatus();
        try {
            return Stream.of(twitter.updateStatus(statusUpdate)).map(s -> {
                TweetyStatus ts = new TweetyStatus(String.valueOf(s.getId()), s.getText(), new TweetyUser(s.getUser().getScreenName(),
                        s.getUser().getName(), s.getUser().getProfileImageURLHttps()), s.getCreatedAt());
                clearCache();
                return ts;
            }).findFirst().get();
        } catch (TwitterException e) {
            logger.error(STATUS_UPDATE_ERROR_MSG, message, e.getMessage(), e);
            if (e.getErrorMessage().equals("Status is a duplicate.")) {
                throw new TweetyException(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
            }
            throw new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }
    }

    private void clearCache() {
        cache.remove(PULL_HOME_TIMELINE_KEY);
        cache.remove(PULL_USER_TIMELINE_KEY);
        cache.remove(FILTER_TWEETS_KEY);
    }

    private void validateMaxLength(String message) throws TweetyException {
        if (message.length() > TweetyConstantsRepository.MAX_TWEET_LENGTH) {
            logger.error(STATUS_UPDATE_ERROR_MSG, message);
            TweetyException exception = new TweetyException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
            cache.put(message, exception);
            throw exception;
        }
    }
}

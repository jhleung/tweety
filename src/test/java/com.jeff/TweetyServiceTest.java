package com.jeff;

import org.junit.Test;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.ResponseList;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TweetyServiceTest {
    private static final Twitter twitter = mock(Twitter.class);
    private static final TweetyService tweetyService = TweetyService.getInstance(twitter);

    @Test
    public void testPullTimelineSuccess() throws TwitterException {
        Status st1 =  TwitterObjectFactory.createStatus("{\"text\":\"st2\"}");
        Status st2 =  TwitterObjectFactory.createStatus("{\"text\":\"st2\"}");
        Status st3 =  TwitterObjectFactory.createStatus("{\"text\":\"st3\"}");
        ResponseList<Status> responseList = new MyResponseList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(twitter.getHomeTimeline()).thenReturn(responseList);

        try {
            List<Status> statusesResult = tweetyService.pullTweets();
            assertEquals(responseList.size(), statusesResult.size());
            IntStream.range(0, responseList.size()).forEach(i -> assertEquals(responseList.get(i).getText(), statusesResult.get(i).getText()));
        } catch (IOException e) {
            assertFalse(false);
        }
    }

    @Test
    public void testPullTimelineFailure() throws TwitterException {
        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.getHomeTimeline()).thenThrow(twitterException);
        try {
            tweetyService.pullTweets();
        } catch (IOException e) {
            assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, e.getMessage());
        }
    }

    @Test
    public void testPublishTweetSuccess() throws TwitterException {
        String message = "value12";

        Status st = mock(Status.class);
        when(st.getText()).thenReturn(message);
        when(twitter.updateStatus(message))
                .thenReturn(
                    TwitterObjectFactory.createStatus(
                            "{\"text\":\"" + message + "\"}"
                    ));
        try {
            Status s = tweetyService.publishTweet(message);
            assertEquals(st.getText(), s.getText());
        } catch (IOException e) {
            assertFalse(false);
        }
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder("");
        IntStream.range(0, TweetyConstantsRepository.MAX_TWEET_LENGTH + 1).forEach(i -> sb.append("x"));
        String message = sb.toString();

        when(twitter.updateStatus(message))
                .thenReturn(
                        TwitterObjectFactory.createStatus(
                                "{\"text\":\"" + message + "\"}"
                        ));
        try {
            tweetyService.publishTweet(message);
        } catch (IOException e) {
            assertEquals(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG, e.getMessage());

        }
    }

    @Test
    public void testPublishEmptyTweet() throws TwitterException {
        String message = "";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        try {
            tweetyService.publishTweet(message);
        } catch (IOException e) {
            assertEquals(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG, e.getMessage());

        }
    }

    @Test
    public void testPublishDuplicateTweet() throws TwitterException{
        String message = "duplicateStatus";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        try {
            tweetyService.publishTweet(message);
        } catch (IOException e) {
            assertEquals(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG, e.getMessage());

        }
    }

    @Test
    public void testPublishTweetGenericException() throws TwitterException{
        String message = "message";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        try {
            tweetyService.publishTweet(message);
        } catch (IOException e) {
            assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, e.getMessage());

        }
    }
}
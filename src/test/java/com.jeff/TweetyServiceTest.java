package com.jeff;

import com.jeff.models.TweetyStatus;
import com.jeff.services.TweetyService;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.ResponseList;
import twitter4j.User;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class TweetyServiceTest {
    private static Twitter twitter = mock(Twitter.class);

    @Test
    public void testPullTimelineSuccess() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        Status st1 = TwitterObjectFactory.createStatus("{\"text\":\"st1\", \"createdAt\":1558379453000," +
                "\"user\":{\"name\":\"sthandle1\", \"screenName\":\"test1\", \"profileImageURLHttps\":\"https://test1.com\"}}");
        Status st2 = TwitterObjectFactory.createStatus("{\"text\":\"st2\", \"createdAt\":1558379453001, " +
                "\"user\":{\"name\":\"sthandle2\", \"screenName\":\"test2\", \"profileImageURLHttps\":\"https://test2.com\"}}");
        Status st3 = TwitterObjectFactory.createStatus("{\"text\":\"st3\", \"createdAt\":1558379453002, " +
                "\"user\":{\"name\":\"sthandle3\", \"screenName\":\"test3\", \"profileImageURLHttps\":\"https:..test3.com\"}}");
        ResponseList<Status> responseList = new MyResponseList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(twitter.getHomeTimeline()).thenReturn(responseList);

        try {
            List<TweetyStatus> statusesResult = tweetyService.pullTweets();
            assertEquals(responseList.size(), statusesResult.size());
            for (int i = 0; i < responseList.size(); i++) {
                Status expected = responseList.get(i);
                TweetyStatus actual = statusesResult.get(i);
                assertEquals(expected.getText(), actual.getMessage());
                assertEquals(expected.getUser().getScreenName(), actual.getUser().getHandle());
                assertEquals(expected.getUser().getName(), actual.getUser().getName());
                assertEquals(expected.getUser().getProfileImageURLHttps(), actual.getUser().getProfileImageUrl());
                assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
            }
        } catch (TweetyException e) {
            assertFalse(false);
        }
        reset(twitter);
    }

    @Test
    public void testPullTimelineFailure() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("test");
        when(twitter.getHomeTimeline()).thenThrow(twitterException);
        try {
            tweetyService.pullTweets();
        } catch (TweetyException e) {
            assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, e.getMessage());
        }
        reset(twitter);
    }

    @Test
    public void filterTweetsSuccessNoMatches() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        Status st1 = TwitterObjectFactory.createStatus("{\"text\":\"st1\", \"createdAt\":1558379453000," +
                "\"user\":{\"name\":\"sthandle1\", \"screenName\":\"test1\", \"profileImageURLHttps\":\"https://test1.com\"}}");
        Status st2 = TwitterObjectFactory.createStatus("{\"text\":\"st2\", \"createdAt\":1558379453001, " +
                "\"user\":{\"name\":\"sthandle2\", \"screenName\":\"test2\", \"profileImageURLHttps\":\"https://test2.com\"}}");
        Status st3 = TwitterObjectFactory.createStatus("{\"text\":\"st3\", \"createdAt\":1558379453002, " +
                "\"user\":{\"name\":\"sthandle3\", \"screenName\":\"test3\", \"profileImageURLHttps\":\"https://test3.com\"}}");
        ResponseList<Status> responseList = new MyResponseList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(twitter.getHomeTimeline()).thenReturn(responseList);

        try {
            List<TweetyStatus> statusesResult = tweetyService.filterTweets("test");
            assertTrue(statusesResult.isEmpty());
        } catch (TweetyException e) {
            assertFalse(false);
        }
        reset(twitter);
    }

    @Test
    public void filterTweetsSuccessMatchesFound() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        Status st1 = TwitterObjectFactory.createStatus("{\"text\":\"st1\", \"createdAt\":1558379453000," +
                "\"user\":{\"name\":\"sthandle1\", \"screenName\":\"test1\", \"profileImageURLHttps\":\"https://test1.com\"}}");
        Status st2 = TwitterObjectFactory.createStatus("{\"text\":\"st2\", \"createdAt\":1558379453001, " +
                "\"user\":{\"name\":\"sthandle2\", \"screenName\":\"test2\", \"profileImageURLHttps\":\"https://test2.com\"}}");
        Status st3 = TwitterObjectFactory.createStatus("{\"text\":\"st3\", \"createdAt\":1558379453002, " +
                "\"user\":{\"name\":\"sthandle3\", \"screenName\":\"test3\", \"profileImageURLHttps\":\"https://test3.com\"}}");
        ResponseList<Status> responseList = new MyResponseList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(twitter.getHomeTimeline()).thenReturn(responseList);
        try {
            List<TweetyStatus> statusesResult = tweetyService.filterTweets("st");
            assertEquals(responseList.size(), statusesResult.size());
            for (int i = 0; i < responseList.size(); i++) {
                Status expected = responseList.get(i);
                TweetyStatus actual = statusesResult.get(i);
                assertEquals(expected.getText(), actual.getMessage());
                assertEquals(expected.getUser().getScreenName(), actual.getUser().getHandle());
                assertEquals(expected.getUser().getName(), actual.getUser().getName());
                assertEquals(expected.getUser().getProfileImageURLHttps(), actual.getUser().getProfileImageUrl());
                assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
            }
        } catch (TweetyException e) {
            assertFalse(false);
        }
        reset(twitter);
    }

    @Test
    public void filterTweetsFailure() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("test");
        when(twitter.getHomeTimeline()).thenThrow(twitterException);
        try {
            tweetyService.filterTweets("test");
        } catch (TweetyException e) {
            assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, e.getMessage());
        }
        reset(twitter);
    }

    @Test
    public void testPublishTweetSuccess() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        String message = "value12";

        Status st = mock(Status.class);
        User u = mock(User.class);
        when(u.getScreenName()).thenReturn("jimmy");
        when(u.getName()).thenReturn("jimmy");
        when(u.getProfileImageURLHttps()).thenReturn("jimmy");
        when(st.getText()).thenReturn(message);
        when(st.getUser()).thenReturn(u);
        when(st.getCreatedAt()).thenReturn(new Date());
        when(twitter.updateStatus(message)).thenReturn(st);

        try {
            TweetyStatus s = tweetyService.publishTweet(message);
            assertEquals(st.getText(), s.getMessage());
            assertEquals(st.getUser().getScreenName(), s.getUser().getHandle());
            assertEquals(st.getUser().getName(), s.getUser().getName());
            assertEquals(st.getUser().getProfileImageURLHttps(), s.getUser().getProfileImageUrl());
            assertEquals(st.getCreatedAt(), s.getCreatedAt());
        } catch (TweetyException e) {
            assertFalse(false);
        }
        reset(twitter);
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
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
        } catch (TweetyException e) {
            assertEquals(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG, e.getMessage());

        }
        reset(twitter);
    }

    @Test
    public void testPublishEmptyTweet() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        String message = "";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("test");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        try {
            tweetyService.publishTweet(message);
        } catch (TweetyException e) {
            assertEquals(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG, e.getMessage());

        }
        reset(twitter);
    }

    @Test
    public void testPublishDuplicateTweet() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        String message = "duplicateStatus";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        try {
            tweetyService.publishTweet(message);
        } catch (TweetyException e) {
            assertEquals(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG, e.getMessage());

        }
        reset(twitter);
    }

    @Test
    public void testPublishTweetGenericException() throws TwitterException {
        TweetyService tweetyService = new TweetyService(twitter);
        String message = "message";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        try {
            tweetyService.publishTweet(message);
        } catch (TweetyException e) {
            assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, e.getMessage());

        }
        reset(twitter);
    }
}
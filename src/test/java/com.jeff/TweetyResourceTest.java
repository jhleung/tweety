package com.jeff;

import com.jeff.models.TweetyStatus;
import com.jeff.resources.TweetyResource;
import org.junit.Test;
import twitter4j.TwitterException;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TweetyResourceTest {
    private static final TweetyService tweetyService = mock(TweetyService.class);
    private static final TweetyResource tweetyResource = new TweetyResource(tweetyService);

    private static final int OK_STATUS_CODE = Response.Status.OK.getStatusCode();
    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

    @Test
    public void testPullTimelineSuccess() throws TweetyException {
        TweetyStatus st1 =  mock(TweetyStatus.class);
        TweetyStatus st2 =  mock(TweetyStatus.class);
        TweetyStatus st3 =  mock(TweetyStatus.class);
        when(st1.getMessage()).thenReturn("st1");
        when(st1.getHandle()).thenReturn("jimmyhandle");
        when(st1.getName()).thenReturn("jimmy");
        when(st1.getProfileImageUrl()).thenReturn("https://jimmy.com");
        when(st1.getCreatedAt()).thenReturn(new Date());

        when(st2.getMessage()).thenReturn("st2");
        when(st2.getHandle()).thenReturn("johnhandle");
        when(st2.getName()).thenReturn("john");
        when(st2.getProfileImageUrl()).thenReturn("https://john.com");
        when(st2.getCreatedAt()).thenReturn(new Date());

        when(st3.getMessage()).thenReturn("st3");
        when(st1.getHandle()).thenReturn("jackhandle");
        when(st1.getName()).thenReturn("jack");
        when(st1.getProfileImageUrl()).thenReturn("https://jack.com");
        when(st1.getCreatedAt()).thenReturn(new Date());

        List<TweetyStatus> responseList = new ArrayList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(tweetyService.pullTweets()).thenReturn(responseList);

        Response response = tweetyResource.pullTweets();
        List<TweetyStatus> statusesResult = (List<TweetyStatus>) response.getEntity();

        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals(responseList.size(), statusesResult.size());
        for (int i = 0; i < responseList.size(); i++) {
            TweetyStatus expected = responseList.get(i);
            TweetyStatus actual = statusesResult.get(i);
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getHandle(), actual.getHandle());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getProfileImageUrl(), actual.getProfileImageUrl());
            assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        }
    }

    @Test
    public void testPullTimelineFailure() throws TweetyException {
        when(tweetyService.pullTweets()).thenThrow(new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.pullTweets();
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetSuccess() throws TweetyException {
        String message = "value12";
        TweetyStatus st = mock(TweetyStatus.class);
        when(st.getMessage()).thenReturn(message);
        when(st.getHandle()).thenReturn("jimmyhandle");
        when(st.getName()).thenReturn("jimmy");
        when(st.getProfileImageUrl()).thenReturn("https://jimmy.com");
        when(st.getCreatedAt()).thenReturn(new Date());

        when(tweetyService.publishTweet(message)).thenReturn(st);

        Response response = tweetyResource.publishTweet(message);
        assertEquals(OK_STATUS_CODE, response.getStatus());

        TweetyStatus tweetyStatus = (TweetyStatus) response.getEntity();
        assertEquals(st.getMessage(), tweetyStatus.getMessage());
        assertEquals(st.getHandle(), tweetyStatus.getHandle());
        assertEquals(st.getName(), tweetyStatus.getName());
        assertEquals(st.getProfileImageUrl(), tweetyStatus.getProfileImageUrl());
        assertEquals(st.getCreatedAt(), tweetyStatus.getCreatedAt());
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TweetyException {
        StringBuilder sb = new StringBuilder("");
        IntStream.range(0, TweetyConstantsRepository.MAX_TWEET_LENGTH + 1).forEach(i -> sb.append("x"));
        String message = sb.toString();

        when(tweetyService.publishTweet(message)).thenThrow(new TweetyException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishEmptyTweet() throws TweetyException {
        String message = "";
        when(tweetyService.publishTweet(message)).thenThrow(new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishDuplicateTweet() throws TweetyException {
        String message = "duplicateStatus";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(tweetyService.publishTweet(message)).thenThrow(new TweetyException(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetGenericException() throws TweetyException {
        String message = "message";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(tweetyService.publishTweet(message)).thenThrow(new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }
}
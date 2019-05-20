package com.jeff;

import com.jeff.resources.TweetyResource;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.ResponseList;

import javax.ws.rs.core.Response;

import java.io.IOException;
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
    public void testPullTimelineSuccess() throws TwitterException, IOException {
        Status st1 =  TwitterObjectFactory.createStatus("{\"text\":\"st2\"}");
        Status st2 =  TwitterObjectFactory.createStatus("{\"text\":\"st2\"}");
        Status st3 =  TwitterObjectFactory.createStatus("{\"text\":\"st3\"}");
        ResponseList<Status> responseList = new MyResponseList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(tweetyService.pullTweets()).thenReturn(responseList);

        Response response = tweetyResource.pullTweets();
        List<Status> statusesResult = (List<Status>) response.getEntity();

        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals(responseList.size(), statusesResult.size());
        IntStream.range(0, responseList.size()).forEach(i -> assertEquals(responseList.get(i).getText(), statusesResult.get(i).getText()));

    }

    @Test
    public void testPullTimelineFailure() throws IOException {
        when(tweetyService.pullTweets()).thenThrow(new IOException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.pullTweets();
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetSuccess() throws TwitterException, IOException {
        String message = "value12";
        Status st = mock(Status.class);
        when(st.getText()).thenReturn(message);
        when(tweetyService.publishTweet(message))
                .thenReturn(
                        TwitterObjectFactory.createStatus(
                                "{\"text\":\"" + message + "\"}"
                        ));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals(st.getText(), ((Status) response.getEntity()).getText());
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws IOException {
        StringBuilder sb = new StringBuilder("");
        IntStream.range(0, TweetyConstantsRepository.MAX_TWEET_LENGTH + 1).forEach(i -> sb.append("x"));
        String message = sb.toString();

        when(tweetyService.publishTweet(message)).thenThrow(new IOException(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishEmptyTweet() throws IOException {
        String message = "";
        when(tweetyService.publishTweet(message)).thenThrow(new IOException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishDuplicateTweet() throws IOException {
        String message = "duplicateStatus";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(tweetyService.publishTweet(message)).thenThrow(new IOException(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetGenericException() throws IOException {
        String message = "message";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(tweetyService.publishTweet(message)).thenThrow(new IOException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }
}
package com.jeff;

import com.jeff.resources.TweetyResource;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.ResponseList;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;


import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TweetyResourceTest {
    private static final Twitter twitter = mock(Twitter.class);

    private static final TweetyResource tweetyResource = new TweetyResource(twitter);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TweetyResource(twitter))
            .build();

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

        Response response = tweetyResource.pullTweets();
        List<Status> statusesResult =(List<Status>) response.getEntity();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(responseList.size(), statusesResult.size());
        IntStream.range(0, responseList.size()).forEach(i -> assertEquals(responseList.get(i).getText(), statusesResult.get(i).getText()));
    }

    @Test
    public void testPullTimelineFailure() throws TwitterException {
        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.getHomeTimeline()).thenThrow(twitterException);
        Response response = tweetyResource.pullTweets();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetSuccess() throws TwitterException, IOException {
        String message = "value12";
        Form formData = new Form();
        formData.param("message", message);

        Status st = mock(Status.class);
        when(st.getText()).thenReturn(message);
        when(twitter.updateStatus(message))
                .thenReturn(
                    TwitterObjectFactory.createStatus(
                            "{\"text\":\"" + message + "\"}"
                    ));
        Response response = tweetyResource.publishTweet(message);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(st.getText(), ((Status) response.getEntity()).getText());
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder("");
        IntStream.range(0, TweetyConstantsRepository.MAX_TWEET_LENGTH + 1).forEach(i -> sb.append("x"));
        String message = sb.toString();
        Form formData = new Form();
        formData.param("message", message);

        when(twitter.updateStatus(message))
                .thenReturn(
                        TwitterObjectFactory.createStatus(
                                "{\"text\":\"" + message + "\"}"
                        ));
        Response response = tweetyResource.publishTweet(message);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishEmptyTweet() throws TwitterException {
        String message = "";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = tweetyResource.publishTweet(message);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishDuplicateTweet() throws TwitterException{
        String message = "duplicateStatus";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = tweetyResource.publishTweet(message);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetGenericException() throws TwitterException{
        String message = "message";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = tweetyResource.publishTweet(message);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }
}
package com.jeff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jeff.resources.TweetyResource;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.ResponseList;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TweetyResourceTest {
    private static final Twitter twitter = mock(Twitter.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TweetyResource(twitter))
            .build();

    public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error. Please contact System Administrator.";
    public static final String DUPLICATE_STATUS_ERROR_MSG = "Status was already posted. Please try again.";
    public static final String EMPTY_STATUS_ERROR_MSG = "Please enter a non-empty status";
    public static final String EXCEED_MAX_LENGTH_ERROR_MSG = "Tweet must be a maximum of 280 characters";
    
    public static final String PUBLISH_TWEET_ENDPOINT = "/api/1.0/twitter/tweet";
    public static final String PULL_TWEETS_ENDPOINT = "/api/1.0/twitter/timeline";

    @Test
    public void testPullTimelineSuccess() throws IOException, TwitterException {
        Status st1 =  TwitterObjectFactory.createStatus("{\"text\":\"st2\"}");
        Status st2 =  TwitterObjectFactory.createStatus("{\"text\":\"st2\"}");
        Status st3 =  TwitterObjectFactory.createStatus("{\"text\":\"st3\"}");
        ResponseList<Status> responseList = new MyResponseList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(twitter.getHomeTimeline()).thenReturn(responseList);
        Response response = resources.target(PULL_TWEETS_ENDPOINT).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        List<String> splittedJsonElements = new ArrayList<String>();
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode jsonNode = jsonMapper.readTree(response.readEntity(String.class));

        if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode individualElement = arrayNode.get(i);
                splittedJsonElements.add(individualElement.toString());
            }
        }

        assertEquals(responseList.size(), splittedJsonElements.size());
        for (int i = 0; i < responseList.size(); i++) {
            assertTrue(splittedJsonElements.get(i).contains(responseList.get(i).getText()));
        }
    }

    @Test
    public void testPullTimelineFailure() throws TwitterException {
        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.getHomeTimeline()).thenThrow(twitterException);
        Response response = resources.target(PULL_TWEETS_ENDPOINT).request().get();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR_MSG, response.readEntity(String.class));
    }

    @Test
    public void testPublishTweetSuccess() throws TwitterException {
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
        Response response = resources.target(PUBLISH_TWEET_ENDPOINT).request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(messageJsonString.contains("\"text\":\"" + message +  "\""));
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TwitterException {
        StringBuilder sb = new StringBuilder("");
        IntStream.range(0, 281).forEach(i -> sb.append("x"));
        String message = sb.toString();
        Form formData = new Form();
        formData.param("message", message);

        when(twitter.updateStatus(message))
                .thenReturn(
                        TwitterObjectFactory.createStatus(
                                "{\"text\":\"" + message + "\"}"
                        ));
        Response response = resources.target(PUBLISH_TWEET_ENDPOINT).request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(EXCEED_MAX_LENGTH_ERROR_MSG, messageJsonString);
    }

    @Test
    public void testPublishEmptyTweet() throws TwitterException {
        String message = "";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = resources.target(PUBLISH_TWEET_ENDPOINT).request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(EMPTY_STATUS_ERROR_MSG, messageJsonString);
    }

    @Test
    public void testPublishDuplicateTweet() throws TwitterException{
        String message = "duplicateStatus";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = resources.target(PUBLISH_TWEET_ENDPOINT).request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(DUPLICATE_STATUS_ERROR_MSG, messageJsonString);
    }

    @Test
    public void testPublishTweetGenericException() throws TwitterException{
        String message = "message";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = resources.target(PUBLISH_TWEET_ENDPOINT).request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR_MSG, messageJsonString);
    }
}
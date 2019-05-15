package com.jeff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jeff.resources.TweetyResource;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import twitter4j.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;


import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class TweetyResourceTest {
    private static final Twitter twitter = mock(Twitter.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TweetyResource(twitter))
            .build();

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
        Response response = resources.target("/api/1.0/twitter/timeline").request().get();
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
        assertEquals(Response.Status.OK.getStatusCode(), resources.target("/api/1.0/twitter/timeline").request().get().getStatus());
        TwitterException twitterException = mock(TwitterException.class);

        when(twitter.getHomeTimeline()).thenThrow(twitterException);
        Response response = resources.target("/api/1.0/twitter/timeline").request().get();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Internal Server Error. Please contact System Administrator.", response.readEntity(String.class));
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
        Response response = resources.target("/api/1.0/twitter/tweet").request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(messageJsonString.contains("\"text\":\"" + message +  "\""));
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TwitterException {
        String message = "thistweetissoincrediblylongthatitexceedsthemaximumlengthoftwohundredandeightyallowedcharactersforasinglegiventweet" +
                         "infactwerenotevenhalfwaythroughatthispointsojustimaginethatandnowweretomakethistweetevenlongerwhichisjustincrediblereally" +
                         "likehonestlyfantasticokaythatsmorethantwohundredandeightcharactersimgoingtoshutup";
        Form formData = new Form();
        formData.param("message", message);

        when(twitter.updateStatus(message))
                .thenReturn(
                        TwitterObjectFactory.createStatus(
                                "{\"text\":\"" + message + "\"}"
                        ));
        Response response = resources.target("/api/1.0/twitter/tweet").request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Tweet must be a maximum of 280 characters", messageJsonString);
    }


    @Test
    public void testPublishEmptyTweet() throws TwitterException {
        String message = "";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = resources.target("/api/1.0/twitter/tweet").request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Please enter a non-empty status", messageJsonString);
    }

    @Test
    public void testPublishDuplicateTweet() throws TwitterException{
        String message = "duplicateStatus";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Status is a duplicate.");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = resources.target("/api/1.0/twitter/tweet").request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Status was already posted. Please try again.", messageJsonString);
    }

    @Test
    public void testPublishTweetGenericException() throws TwitterException{
        String message = "message";
        Form formData = new Form();
        formData.param("message", message);

        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(twitter.updateStatus(message)).thenThrow(twitterException);

        Response response = resources.target("/api/1.0/twitter/tweet").request().post(Entity.form(formData));
        String messageJsonString = response.readEntity(String.class);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Internal Server Error. Please contact System Administrator.", messageJsonString);
    }

    private class MyResponseList<T> implements ResponseList<T> {
        private List<T> list = new ArrayList<>();

        @Override
        public boolean add(T t) {
            list.add(t);
            return true;
        }
        @Override
        public T get(int index) {
            return list.get(index);
        }
        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }
        @Override
        public int size() {
            return list.size();
        }

        @Override
        public RateLimitStatus getRateLimitStatus() { return null; }
        @Override
        public int getAccessLevel() { return 0; }
        @Override
        public boolean isEmpty() { return false; }
        @Override
        public boolean contains(Object o) { return false; }
        @Override
        public Object[] toArray() { return new Object[0]; }
        @Override
        public <T1> T1[] toArray(T1[] a) { return null; }
        @Override
        public boolean remove(Object o) { return false; }
        @Override
        public boolean containsAll(Collection<?> c) { return false; }
        @Override
        public boolean addAll(Collection<? extends T> c) { return false; }
        @Override
        public boolean addAll(int index, Collection<? extends T> c) { return false; }
        @Override
        public boolean removeAll(Collection<?> c) {  return false; }
        @Override
        public boolean retainAll(Collection<?> c) { return false; }
        @Override
        public void clear() { }
        @Override
        public T set(int index, T element) { return null; }
        @Override
        public void add(int index, T element) { }
        @Override
        public T remove(int index) { return null; }
        @Override
        public int indexOf(Object o) { return 0;  }
        @Override
        public int lastIndexOf(Object o) { return 0; }
        @Override
        public ListIterator<T> listIterator() { return null; }
        @Override
        public ListIterator<T> listIterator(int index) { return null; }
        @Override
        public List<T> subList(int fromIndex, int toIndex) { return null; }
    }
}
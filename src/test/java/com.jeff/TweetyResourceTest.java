package com.jeff;

import com.jeff.models.TweetyStatus;
import com.jeff.models.TweetyUser;
import com.jeff.resources.TweetyResource;
import com.jeff.services.TweetyService;
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

    private static final int OK_STATUS_CODE = Response.Status.OK.getStatusCode();
    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

    @Test
    public void testPullHomeTimelineSuccess() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        TweetyStatus st1 =  mockTweetyStatus("st1", "jimmyhandle", "jimmy", "https://jimmy.com", new Date());
        TweetyStatus st2 =  mockTweetyStatus("st2", "johnhandle", "john", "https://john.com", new Date());
        TweetyStatus st3 =  mockTweetyStatus("st3", "jackhandle", "jack", "https://jack.com", new Date());

        List<TweetyStatus> responseList = new ArrayList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(tweetyService.pullHomeTimeline()).thenReturn(responseList);

        Response response = tweetyResource.pullHomeTimeline();
        List<TweetyStatus> statusesResult = (List<TweetyStatus>) response.getEntity();

        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals(responseList.size(), statusesResult.size());
        for (int i = 0; i < responseList.size(); i++) {
            TweetyStatus expected = responseList.get(i);
            TweetyStatus actual = statusesResult.get(i);
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getUser().getHandle(), actual.getUser().getHandle());
            assertEquals(expected.getUser().getName(), actual.getUser().getName());
            assertEquals(expected.getUser().getProfileImageUrl(), actual.getUser().getProfileImageUrl());
            assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        }
    }

    @Test
    public void testPullHomeTimelineFailure() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        when(tweetyService.pullHomeTimeline()).thenThrow(new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.pullHomeTimeline();
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }


    @Test
    public void testPullUserTimelineSuccess() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        TweetyStatus st1 =  mockTweetyStatus("st1", "jimmyhandle", "jimmy", "https://jimmy.com", new Date());
        TweetyStatus st2 =  mockTweetyStatus("st2", "johnhandle", "john", "https://john.com", new Date());
        TweetyStatus st3 =  mockTweetyStatus("st3", "jackhandle", "jack", "https://jack.com", new Date());

        List<TweetyStatus> responseList = new ArrayList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(tweetyService.pullUserTimeline()).thenReturn(responseList);

        Response response = tweetyResource.pullUserTimeline();
        List<TweetyStatus> statusesResult = (List<TweetyStatus>) response.getEntity();

        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals(responseList.size(), statusesResult.size());
        for (int i = 0; i < responseList.size(); i++) {
            TweetyStatus expected = responseList.get(i);
            TweetyStatus actual = statusesResult.get(i);
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getUser().getHandle(), actual.getUser().getHandle());
            assertEquals(expected.getUser().getName(), actual.getUser().getName());
            assertEquals(expected.getUser().getProfileImageUrl(), actual.getUser().getProfileImageUrl());
            assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        }
    }

    @Test
    public void testPullUserTimelineFailure() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        when(tweetyService.pullUserTimeline()).thenThrow(new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.pullUserTimeline();
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testFilterTweetsSuccessNoMatch() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        TweetyStatus st1 =  mockTweetyStatus("st1", "jimmyhandle", "jimmy", "https://jimmy.com", new Date());
        TweetyStatus st2 =  mockTweetyStatus("st2", "johnhandle", "john", "https://john.com", new Date());
        TweetyStatus st3 =  mockTweetyStatus("st3", "jackhandle", "jack", "https://jack.com", new Date());

        List<TweetyStatus> responseList = new ArrayList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(tweetyService.filterTweets("test")).thenReturn(new ArrayList<>());

        Response response = tweetyResource.filterTweets("test");

        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals("No results were found", response.getEntity());
    }

    @Test
    public void testFilterTweetsSuccessMatchFound() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        TweetyStatus st1 =  mockTweetyStatus("st1", "jimmyhandle", "jimmy", "https://jimmy.com", new Date());
        TweetyStatus st2 =  mockTweetyStatus("st2", "johnhandle", "john", "https://john.com", new Date());
        TweetyStatus st3 =  mockTweetyStatus("st3", "jackhandle", "jack", "https://jack.com", new Date());

        List<TweetyStatus> responseList = new ArrayList<>();
        responseList.add(st1);
        responseList.add(st2);
        responseList.add(st3);

        when(tweetyService.filterTweets("st")).thenReturn(responseList);

        Response response = tweetyResource.filterTweets("st");
        List<TweetyStatus> statusesResult = (ArrayList<TweetyStatus>) response.getEntity();

        assertEquals(OK_STATUS_CODE, response.getStatus());
        assertEquals(responseList.size(), statusesResult.size());
        for (int i = 0; i < responseList.size(); i++) {
            TweetyStatus expected = responseList.get(i);
            TweetyStatus actual = statusesResult.get(i);
            assertEquals(expected.getMessage(), actual.getMessage());
            assertEquals(expected.getUser().getHandle(), actual.getUser().getHandle());
            assertEquals(expected.getUser().getName(), actual.getUser().getName());
            assertEquals(expected.getUser().getProfileImageUrl(), actual.getUser().getProfileImageUrl());
            assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        }
    }

    @Test
    public void testFilterTweetsFailure() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        when(tweetyService.filterTweets("test")).thenThrow(new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.filterTweets("test");
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishTweetSuccess() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        String message = "value12";
        TweetyStatus st = mockTweetyStatus(message, "jimmyhandle", "jimmy", "https://jimmy.com", new Date());

        when(tweetyService.publishTweet(message)).thenReturn(st);

        Response response = tweetyResource.publishTweet(message);
        assertEquals(OK_STATUS_CODE, response.getStatus());

        TweetyStatus tweetyStatus = (TweetyStatus) response.getEntity();
        assertEquals(st.getMessage(), tweetyStatus.getMessage());
        assertEquals(st.getUser().getHandle(), tweetyStatus.getUser().getHandle());
        assertEquals(st.getUser().getName(), tweetyStatus.getUser().getName());
        assertEquals(st.getUser().getProfileImageUrl(), tweetyStatus.getUser().getProfileImageUrl());
        assertEquals(st.getCreatedAt(), tweetyStatus.getCreatedAt());
    }

    @Test
    public void testPublishTweetExceedMaxLength() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
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
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        String message = "";
        when(tweetyService.publishTweet(message)).thenThrow(new TweetyException(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testPublishDuplicateTweet() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
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
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        String message = "message";
        TwitterException twitterException = mock(TwitterException.class);
        when(twitterException.getErrorMessage()).thenReturn("Unauthorized");
        when(tweetyService.publishTweet(message)).thenThrow(new TweetyException(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG));

        Response response = tweetyResource.publishTweet(message);
        assertEquals(INTERNAL_SERVER_ERROR_STATUS_CODE, response.getStatus());
        assertEquals(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG, response.getEntity());
    }

    @Test
    public void testReplyTweetSuccess() throws TweetyException {
        TweetyResource tweetyResource = new TweetyResource(tweetyService);
        String message = "value12";
        String tweetId = "12";
        TweetyStatus st = mockTweetyStatus(message, "jimmyhandle", "jimmy", "https://jimmy.com", new Date());

        when(tweetyService.replyTweet(message, tweetId)).thenReturn(st);

        Response response = tweetyResource.replyTweet(message, tweetId);
        assertEquals(OK_STATUS_CODE, response.getStatus());

        TweetyStatus tweetyStatus = (TweetyStatus) response.getEntity();
        assertEquals(st.getMessage(), tweetyStatus.getMessage());
        assertEquals(st.getUser().getHandle(), tweetyStatus.getUser().getHandle());
        assertEquals(st.getUser().getName(), tweetyStatus.getUser().getName());
        assertEquals(st.getUser().getProfileImageUrl(), tweetyStatus.getUser().getProfileImageUrl());
        assertEquals(st.getCreatedAt(), tweetyStatus.getCreatedAt());
    }

    private TweetyStatus mockTweetyStatus(String message, String handle, String name, String profileImageUrl, Date createdAt) {
        TweetyStatus st =  mock(TweetyStatus.class);
        when(st.getMessage()).thenReturn(message);
        TweetyUser u = mock(TweetyUser.class);
        when(st.getUser()).thenReturn(u);
        when(u.getHandle()).thenReturn(handle);
        when(u.getName()).thenReturn(name);
        when(u.getProfileImageUrl()).thenReturn(profileImageUrl);
        when(st.getCreatedAt()).thenReturn(createdAt);
        return st;
    }
}
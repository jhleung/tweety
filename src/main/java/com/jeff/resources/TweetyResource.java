package com.jeff.resources;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/1.0/twitter")
public class TweetyResource {
    private static final int MAX_TWEET_LENGTH = 280;

    private final Twitter twitter;

    public TweetyResource(Twitter t) {
        twitter = t;
    }

    @POST
    @Path("/tweet")
    public Response publishTweet(String tweet) {
        Response.Status responseStatus = Response.Status.OK;
        String responseMsg = "";
        validateLength(tweet);
        try {
            twitter.updateStatus(tweet);
        } catch (TwitterException e){
            if (!isAuthorized(e.getErrorCode())) {
                responseMsg = e.getErrorMessage();
            }
            responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
        }
        return Response
                .status(responseStatus)
                .entity(responseMsg)
                .build();
    }

    @GET
    @Path("/timeline")
    public Response pullTweets() {
        System.out.println("test");
        Response.Status responseStatus = Response.Status.OK;
        String responseMsg = "";
        try {
           List<Status> statuses = twitter.getHomeTimeline();
           StringBuilder sb = new StringBuilder();
           statuses.forEach((val) -> sb.append("@" + val.getUser().getScreenName() + " - " + val.getText() + "\n"));
           responseMsg = sb.toString();
       } catch (TwitterException e) {
            if (!isAuthorized(e.getErrorCode())) {
                responseMsg = e.getErrorMessage();
            }
           responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
        }
        return Response
                .status(responseStatus)
                .entity(responseMsg)
                .build();
    }

    private boolean isAuthorized(int errorCode) {
        return errorCode == Response.Status.UNAUTHORIZED.getStatusCode();
    }

    private boolean validateLength(String status) {
        return status.length() <= MAX_TWEET_LENGTH;
    }
}

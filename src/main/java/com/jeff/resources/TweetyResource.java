package com.jeff.resources;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/tweet")
    public Response publishTweet(String tweet) {
        Response.Status responseStatus = Response.Status.OK;
        Response.ResponseBuilder rb = Response.status(responseStatus);

        if (!validateLength(tweet)) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity("Tweet must be a maximum of 280 characters");
        } else {
            try {
                Status status = twitter.updateStatus(tweet);
                rb.entity(status);
            } catch (TwitterException e) {
                rb.status( Response.Status.INTERNAL_SERVER_ERROR);
                if (!isAuthorized(e.getStatusCode())) {
                    rb.entity(e.getErrorMessage());
                }
            }
        }

        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/timeline")
    public Response pullTweets() {
        Response.Status responseStatus = Response.Status.OK;
        Response.ResponseBuilder rb = Response.status(responseStatus);

        try {
            List<Status> statuses = twitter.getHomeTimeline();
            rb.entity(statuses);
       } catch (TwitterException e) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            if (!isAuthorized(e.getStatusCode())) {
                rb.entity(e.getErrorMessage());
            }
        }

        return rb.build();
    }

    private boolean isAuthorized(int errorCode) {
        return errorCode == Response.Status.UNAUTHORIZED.getStatusCode();
    }

    private boolean validateLength(String status) {
        return status.length() <= MAX_TWEET_LENGTH;
    }
}

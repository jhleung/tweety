package com.jeff.resources;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.*;
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
    public Response publishTweet(@FormParam("message") String tweet) {
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
                if (tweet.isEmpty()) {
                    rb.entity("Please enter a non-empty status");
                } else if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    rb.entity("Status was already posted. Please try again.");
                } else {
                    rb.entity("Internal Server Error. Please contact System Administrator.");
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
            rb.entity("Internal Server Error. Please contact System Administrator.");
        }

        return rb.build();
    }

    private boolean validateLength(String status) {
        return status.length() <= MAX_TWEET_LENGTH;
    }
}

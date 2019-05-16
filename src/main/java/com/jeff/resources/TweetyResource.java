package com.jeff.resources;

import com.jeff.TweetyConstantsRepository;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/1.0/twitter")
public class TweetyResource {

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
            rb.entity(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
        } else {
            try {
                Status status = twitter.updateStatus(tweet);
                rb.entity(status);
            } catch (TwitterException e) {
                rb.status(Response.Status.INTERNAL_SERVER_ERROR);
                if (tweet.isEmpty()) {
                    rb.entity(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
                } else if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    rb.entity(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
                } else {
                    rb.entity(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
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
            rb.entity(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
        }

        return rb.build();
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

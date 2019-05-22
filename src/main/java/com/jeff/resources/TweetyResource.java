package com.jeff.resources;

import com.jeff.TweetyException;
import com.jeff.TweetyService;
import com.jeff.models.TweetyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/api/1.0")
public class TweetyResource {
    private final TweetyService tweetyService;

    private static final Logger logger = LoggerFactory.getLogger(TweetyResource.class);

    public TweetyResource(TweetyService ts) {
        tweetyService = ts;
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/tweet")
    public Response publishTweet(@FormParam("message") String message) {
        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");
        final Response.ResponseBuilder rb = Response.status(Response.Status.OK);

        try {
            rb.entity(tweetyService.publishTweet(message));
            logger.info("Message \"{}\" published successfully", message);
        } catch (TweetyException e) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(e.getMessage());
        }

        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/timeline")
    public Response pullTweets() {
        logger.trace("/api/1.0/twitter/timeline endpoint hit with GET request. Attempting to pull home timeline...");
        final Response.ResponseBuilder rb = Response.status(Response.Status.OK);
        try {
            rb.entity(tweetyService.pullTweets());
        } catch (TweetyException e) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(e.getMessage());
        }

        logger.trace("Reached end of GET request to /api/1.0/twitter/timeline");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/timeline/filter")
    public Response filterTweets(@QueryParam("keyword") String keyword) {
        logger.trace("/api/1.0/timeline/filter endpoint hit with GET request. Attempting to pull home timeline and apply filter...");
        final Response.ResponseBuilder rb = Response.status(Response.Status.OK);
        try {
            Optional<List<TweetyStatus>> optionalList = tweetyService.filterTweets(keyword);
            rb.entity(optionalList.isPresent() ? optionalList.get() : new ArrayList<TweetyStatus>());
        } catch (TweetyException e) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(e.getMessage());
        }

        logger.trace("Reached end of GET request to /api/1.0/timeline/filter");
        return rb.build();
    }
}

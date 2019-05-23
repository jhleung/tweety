package com.jeff.resources;

import com.jeff.TweetyException;
import com.jeff.TweetyService;
import com.jeff.models.TweetyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
        TweetyResponseBuilder tweetyResponseBuilder = () -> {
            final Response.ResponseBuilder rb = Response.status(Response.Status.OK);

            try {
                rb.entity(tweetyService.publishTweet(message));
                logger.info("Message \"{}\" published successfully", message);
            } catch (TweetyException e) {
                rb.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
            }

            return rb.build();
        };
        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");

        return tweetyResponseBuilder.buildTweetyResponse();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/timeline")
    public Response pullTweets() {
        logger.trace("/api/1.0/twitter/timeline endpoint hit with GET request. Attempting to pull home timeline...");
        TweetyResponseBuilder tweetyResponseBuilder = () -> {
            final Response.ResponseBuilder rb = Response.status(Response.Status.OK);
            try {
                rb.entity(tweetyService.pullTweets());
            } catch (TweetyException e) {
                rb.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
            }
            return rb.build();
        };

        logger.trace("Reached end of GET request to /api/1.0/twitter/timeline");
        return tweetyResponseBuilder.buildTweetyResponse();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/timeline/filter")
    public Response filterTweets(@QueryParam("keyword") String keyword) {
        logger.trace("/api/1.0/timeline/filter endpoint hit with GET request. Attempting to pull home timeline and apply filter...");
        TweetyResponseBuilder tweetyResponseBuilder = () -> {
            final Response.ResponseBuilder rb = Response.status(Response.Status.OK);
            try {
                final List<TweetyStatus> tweetyStatuses = tweetyService.filterTweets(keyword);
                rb.entity(tweetyStatuses.isEmpty() ? "No results were found" : tweetyStatuses);
            } catch (TweetyException e) {
                rb.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
            }
            return rb.build();
        };

        logger.trace("Reached end of GET request to /api/1.0/timeline/filter");
        return tweetyResponseBuilder.buildTweetyResponse();
    }

    interface TweetyResponseBuilder {
        Response buildTweetyResponse();
    }
}


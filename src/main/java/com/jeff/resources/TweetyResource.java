package com.jeff.resources;

import com.jeff.TweetyException;
import com.jeff.models.TweetyStatus;
import com.jeff.services.TweetyService;
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

    private static final TweetyResponseBuilder tweetyResponseBuilder = (s, e) -> Response.status(s).entity(e);

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/tweet")
    public Response publishTweet(@FormParam("message") String message) {
        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");

        Response.ResponseBuilder rb;
        try {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK, tweetyService.publishTweet(message));
            logger.info("Message \"{}\" published successfully", message);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/timeline")
    public Response pullTweets() {
        logger.trace("/api/1.0/twitter/timeline endpoint hit with GET request. Attempting to pull home timeline...");

        Response.ResponseBuilder rb;
        try {
            List<TweetyStatus> tweetyStatuses = tweetyService.pullTweets();
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK, tweetyStatuses);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        logger.trace("Reached end of GET request to /api/1.0/twitter/timeline");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/timeline/filter")
    public Response filterTweets(@QueryParam("keyword") String keyword) {
        logger.trace("/api/1.0/timeline/filter endpoint hit with GET request. Attempting to pull home timeline and apply filter...");

        Response.ResponseBuilder rb;
        try {
            List<TweetyStatus> tweetyStatuses = tweetyService.filterTweets(keyword);
            if (tweetyStatuses.isEmpty())
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK, "No results were found");
            else
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK, tweetyStatuses);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        logger.trace("Reached end of GET request to /api/1.0/timeline/failure");
        return rb.build();
    }

    interface TweetyResponseBuilder {
        Response.ResponseBuilder buildTweetyResponse(Response.Status status, Object o);
    }

}


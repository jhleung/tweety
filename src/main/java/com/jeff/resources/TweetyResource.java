package com.jeff.resources;

import com.jeff.TweetyCache;
import com.jeff.TweetyException;
import com.jeff.models.TweetyStatus;
import com.jeff.services.TweetyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/api/1.0")
public class TweetyResource {
    private final TweetyCache cache;
    private final TweetyService tweetyService;

    private static final Logger logger = LoggerFactory.getLogger(TweetyResource.class);

    @Inject
    public TweetyResource(TweetyService ts) {
        cache = new TweetyCache();
        tweetyService = ts;
    }

    private final TweetyResponseBuilder tweetyResponseBuilder = (s, e) -> Response.status(s).entity(e).lastModified(new Date());

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/tweet")
    public synchronized Response publishTweet(@FormParam("message") String message) {
        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");
        Response.ResponseBuilder rb;
        if (cache.contains(message)) {
            Response r = cache.get(message);
            rb = tweetyResponseBuilder.buildTweetyResponse(r.getStatus(), r.getEntity());
        } else {
            try {
                TweetyStatus tweetyStatus = tweetyService.publishTweet(message);
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatus);
                logger.info("Message \"{}\" published successfully", message);
            } catch (TweetyException e) {
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
            }
            cache.put(message, rb.build());
        }
        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/timeline")
    public synchronized Response pullTweets() {
        logger.trace("/api/1.0/twitter/timeline endpoint hit with GET request. Attempting to pull home timeline...");
        Response.ResponseBuilder rb;
        if (cache.useCachedTimeline()) {
            final Response timeline = cache.getTimeline();
            rb = tweetyResponseBuilder.buildTweetyResponse(timeline.getStatus(), timeline.getEntity());
        } else {
            try {
                List<TweetyStatus> tweetyStatuses = tweetyService.pullTweets();
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatuses);
            } catch (TweetyException e) {
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
            }
            cache.setTimeline(rb.build());
        }
        logger.trace("Reached end of GET request to /api/1.0/twitter/timeline");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/timeline/filter")
    public synchronized Response filterTweets(@QueryParam("keyword") String keyword) {
        logger.trace("/api/1.0/timeline/filter endpoint hit with GET request. Attempting to pull home timeline and apply filter...");
        Response.ResponseBuilder rb;
        if (cache.useCachedFilteredTimeline()) {
            final Response filteredTimeline = cache.getFilteredTimeline();
            rb = tweetyResponseBuilder.buildTweetyResponse(filteredTimeline.getStatus(), filteredTimeline.getEntity());
        } else {
            try {
                List<TweetyStatus> tweetyStatuses = tweetyService.filterTweets(keyword);
                if (tweetyStatuses.isEmpty())
                    rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), "No results were found");
                else
                    rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatuses);
            } catch (TweetyException e) {
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
            }
            cache.setFilteredTimeline(rb.build());
        }
        logger.trace("Reached end of GET request to /api/1.0/timeline/failure");
        return rb.build();
    }

    interface TweetyResponseBuilder {
        Response.ResponseBuilder buildTweetyResponse(int status, Object o);
    }

}


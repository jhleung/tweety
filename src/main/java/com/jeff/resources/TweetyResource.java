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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/api/1.0")
public class TweetyResource {
    private final TweetyCache cache;

    private String publishedTweetSincePull, publishedTweetSinceFilter;

    private final TweetyService tweetyService;

    private static final String PUBLISH_TWEET_SUCCESS_MSG = "SUCCESS";
    private static final String PUBLISH_TWEET_PATH = "/twitter/tweet";
    private static final String PULL_TWEETS_PATH = "/twitter/timeline";
    private static final String FILTER_TWEETS_PATH = "/timeline/filter";
    private static final String PULL_TWEETS_CACHE_KEY = "HOME_TIMELINE";
    private static final String FILTER_TWEETS_CACHE_KEY = "FILTERED_TIMELINE";

    private static final Logger logger = LoggerFactory.getLogger(TweetyResource.class);

    @Inject
    public TweetyResource(TweetyService ts) {
        cache = new TweetyCache(TimeUnit.DAYS.toMillis(1));
        tweetyService = ts;
    }

    private final TweetyResponseBuilder tweetyResponseBuilder = (s, e) -> Response.status(s).entity(e);

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(PUBLISH_TWEET_PATH)
    public synchronized Response publishTweet(@FormParam("message") String message) {
        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");
        Response.ResponseBuilder rb;
        if (cache.contains(message) && !cache.get(message).equals(PUBLISH_TWEET_SUCCESS_MSG)) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), cache.get(message));
        } else {
            try {
                TweetyStatus tweetyStatus = tweetyService.publishTweet(message);
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatus);
                cache.put(message, PUBLISH_TWEET_SUCCESS_MSG);
                logger.info("Message \"{}\" published successfully", message);
            } catch (TweetyException e) {
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
                cache.put(message, e.getMessage());
            }
        }
        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(PULL_TWEETS_PATH)
    public synchronized Response pullTweets() {
        logger.trace("/api/1.0/twitter/timeline endpoint hit with GET request. Attempting to pull home timeline...");
        Response.ResponseBuilder rb;
        Response r = (Response) cache.get(PULL_TWEETS_CACHE_KEY);
        if (publishedTweetSincePull == cache.getTail() && r != null) {
            rb = tweetyResponseBuilder.buildTweetyResponse(r.getStatus(), r.getEntity());
        } else {
            try {
                List<TweetyStatus> tweetyStatuses = tweetyService.pullTweets();
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatuses);
            } catch (TweetyException e) {
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
            }
            cache.put(PULL_TWEETS_CACHE_KEY, rb.build());
            publishedTweetSincePull = (String) cache.getTail();
        }
        logger.trace("Reached end of GET request to /api/1.0/twitter/timeline");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(FILTER_TWEETS_PATH)
    public synchronized Response filterTweets(@QueryParam("keyword") String keyword) {
        logger.trace("/api/1.0/timeline/filter endpoint hit with GET request. Attempting to pull home timeline and apply filter...");
        Response.ResponseBuilder rb;
        Response r = (Response) cache.get(FILTER_TWEETS_CACHE_KEY);
        if (publishedTweetSinceFilter == cache.getTail() && r != null) {
            rb = tweetyResponseBuilder.buildTweetyResponse(r.getStatus(), r.getEntity());
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
            cache.put(FILTER_TWEETS_CACHE_KEY, rb.build());
            publishedTweetSinceFilter = (String) cache.getTail();
        }
        logger.trace("Reached end of GET request to /api/1.0/timeline/failure");
        return rb.build();
    }

    interface TweetyResponseBuilder {
        Response.ResponseBuilder buildTweetyResponse(int status, Object o);
    }

}


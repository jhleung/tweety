package com.jeff.resources;

import com.jeff.TweetyConstantsRepository;
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

@Path("/api/1.0")
public class TweetyResource {
    private final TweetyService tweetyService;

    private static final Logger logger = LoggerFactory.getLogger(TweetyResource.class);

    @Inject
    public TweetyResource(TweetyService ts) {
        tweetyService = ts;
    }

    private final TweetyResponseBuilder tweetyResponseBuilder = (s, e) -> Response.status(s).entity(e);

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/tweet")
    public Response publishTweet(@FormParam("message") String message) {
        if (message == null)
            return tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), TweetyConstantsRepository.NULL_STATUS_ERROR_MSG).build();

        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");
        Response.ResponseBuilder rb;

        try {
            TweetyStatus tweetyStatus = tweetyService.publishTweet(message);
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatus);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
        }
        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");
        return rb.build();
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/reply")
    public Response replyTweet(@FormParam("message") String message, @FormParam("inReplyToId") String inReplyToId) {
        if (message == null)
            return tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), TweetyConstantsRepository.NULL_STATUS_ERROR_MSG).build();
        if (inReplyToId == null)
            return tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), TweetyConstantsRepository.NULL_TWEET_ID_ERROR_MSG).build();

        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");
        Response.ResponseBuilder rb;

        try {
            TweetyStatus tweetyStatus = tweetyService.replyTweet(message, inReplyToId);
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatus);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
        }
        logger.trace("Reached end of POST request to /api/1.0/twitter/reply");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/homeTimeline")
    public Response pullHomeTimeline() {
        logger.trace("/api/1.0/twitter/homeTimeline endpoint hit with GET request. Attempting to pull home timeline...");
        Response.ResponseBuilder rb;
        try {
            List<TweetyStatus> tweetyStatuses = tweetyService.pullHomeTimeline();
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatuses);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
        }
        logger.trace("Reached end of GET request to /api/1.0/twitter/homeTimeline");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/twitter/userTimeline")
    public Response pullUserTimeline() {
        logger.trace("/api/1.0/twitter/userTimeline endpoint hit with GET request. Attempting to pull user timeline...");
        Response.ResponseBuilder rb;
        try {
            List<TweetyStatus> tweetyStatuses = tweetyService.pullUserTimeline();
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatuses);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
        }
        logger.trace("Reached end of GET request to /api/1.0/twitter/userTimeline");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/homeTimeline/filter")
    public Response filterTweets(@QueryParam("keyword") String keyword) {
        if (keyword == null)
            return tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), TweetyConstantsRepository.NULL_KEYWORD_ERROR_MSG).build();

        logger.trace("/api/1.0/timeline/filter endpoint hit with GET request. Attempting to pull home timeline and apply filter...");
        Response.ResponseBuilder rb;

        try {
            List<TweetyStatus> tweetyStatuses = tweetyService.filterTweets(keyword);
            if (tweetyStatuses.isEmpty())
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), "No results were found");
            else
                rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.OK.getStatusCode(), tweetyStatuses);
        } catch (TweetyException e) {
            rb = tweetyResponseBuilder.buildTweetyResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
        }
        logger.trace("Reached end of GET request to /api/1.0/timeline/failure");
        return rb.build();
    }

    interface TweetyResponseBuilder {
        Response.ResponseBuilder buildTweetyResponse(int status, Object o);
    }

}


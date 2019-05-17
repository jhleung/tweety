package com.jeff.resources;

import com.jeff.TweetyConstantsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/1.0/twitter")
public class TweetyResource {

    private final Twitter twitter;

    private static final Logger logger = LoggerFactory.getLogger(TweetyResource.class);
    private static final String PUBLISH_TWEET_ERROR_MSG = "Message \"{}\" was not published successfully.";

    public TweetyResource(Twitter t) {
        twitter = t;
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/tweet")
    public Response publishTweet(@FormParam("message") String message) {
        logger.trace("/api/1.0/twitter/tweet endpoint hit with POST request. Attempting to publish message...");
        logger.debug("Message to be published: \"{}\"", message);
        Response.ResponseBuilder rb = Response.status(Response.Status.OK);

        if (!validateLength(message)) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(TweetyConstantsRepository.EXCEED_MAX_LENGTH_ERROR_MSG);
            logger.error(PUBLISH_TWEET_ERROR_MSG, message);
        } else {
            try {
                rb.entity(twitter.updateStatus(message));
                logger.info("Message \"{}\" published successfully", message);
            } catch (TwitterException e) {
                rb.status(Response.Status.INTERNAL_SERVER_ERROR);
                logger.error(PUBLISH_TWEET_ERROR_MSG, message, e.getErrorMessage(), e);
                if (message.isEmpty()) {
                    rb.entity(TweetyConstantsRepository.EMPTY_STATUS_ERROR_MSG);
                } else if (e.getErrorMessage().equals("Status is a duplicate.")) {
                    rb.entity(TweetyConstantsRepository.DUPLICATE_STATUS_ERROR_MSG);
                } else {
                    rb.entity(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
                }
            }
        }

        logger.trace("Reached end of POST request to /api/1.0/twitter/tweet");
        return rb.build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/timeline")
    public Response pullTweets() {
        logger.trace("/api/1.0/twitter/timeline endpoint hit with GET request. Attempting to pull home timeline...");
        Response.ResponseBuilder rb = Response.status(Response.Status.OK);

        try {
            rb.entity(twitter.getHomeTimeline());
            logger.info("Home timeline pulled successfully. See log timestamp to see what date the timeline was pulled.");
        } catch (TwitterException e) {
            rb.status(Response.Status.INTERNAL_SERVER_ERROR);
            rb.entity(TweetyConstantsRepository.INTERNAL_SERVER_ERROR_MSG);
            logger.error("Timeline was not pulled successfully. {}", e.getErrorMessage(), e);
        }

        logger.trace("Reached end of GET request to /api/1.0/twitter/timeline");
        return rb.build();
    }

    private boolean validateLength(String status) {
        return status.length() <= TweetyConstantsRepository.MAX_TWEET_LENGTH;
    }
}

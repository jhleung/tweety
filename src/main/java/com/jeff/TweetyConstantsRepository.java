package com.jeff;

import javax.ws.rs.core.Response;

public class TweetyConstantsRepository {
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error. Please contact System Administrator.";
    public static final String DUPLICATE_STATUS_ERROR_MSG = "Status was already posted. Please try again.";
    public static final String EMPTY_STATUS_ERROR_MSG = "Please enter a non-empty status.";
    public static final String EXCEED_MAX_LENGTH_ERROR_MSG = "Tweet must be a maximum of " + MAX_TWEET_LENGTH + " characters.";

    public static final Response.Status INTERNAL_SERVER_ERROR_STATUS = Response.Status.INTERNAL_SERVER_ERROR;
    public static final Response.Status OK_STATUS = Response.Status.OK;

}

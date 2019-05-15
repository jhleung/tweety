package com.jeff;

public class TweetyConstantsRepository {
    public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error. Please contact System Administrator.";
    public static final String DUPLICATE_STATUS_ERROR_MSG = "Status was already posted. Please try again.";
    public static final String EMPTY_STATUS_ERROR_MSG = "Please enter a non-empty status";
    public static final String EXCEED_MAX_LENGTH_ERROR_MSG = "Tweet must be a maximum of 280 characters";
    public static final int MAX_TWEET_LENGTH = 280;
}
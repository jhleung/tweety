package com.jeff;

import com.jeff.services.TweetyService;
import dagger.Module;
import dagger.Provides;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Module
public class TweetyModule {
    private final TweetyConfiguration conf;

    public TweetyModule(TweetyConfiguration c) {
        conf = c;
    }

    @Provides
    Configuration provideConfiguration() {
        TwitterAuthorizationConfiguration twitterAuthorizationConfiguration = conf.getTwitter4jAuthorizationConfiguration();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(twitterAuthorizationConfiguration.getDebug())
                .setOAuthConsumerKey(twitterAuthorizationConfiguration.getOAuthConsumerKey())
                .setOAuthConsumerSecret(twitterAuthorizationConfiguration.getOAuthConsumerSecret())
                .setOAuthAccessToken(twitterAuthorizationConfiguration.getOAuthAccessToken())
                .setOAuthAccessTokenSecret(twitterAuthorizationConfiguration.getOAuthAccessTokenSecret());
        return cb.build();
    }

    @Provides
    Twitter provideTwitter(Configuration c) {
        return new TwitterFactory(c).getInstance();
    }

    @Provides
    TweetyService provideTweetyService(Twitter t) {
        return TweetyService.getInstance(t);
    }
}

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TwitterHelper {
    public static Configuration getConfiguration() throws IOException {
        Properties prop = new Properties();
        try (InputStream in = PublishTweet.class.getResourceAsStream("/config.properties")) {
            prop.load(in);
            String consumerKey = prop.getProperty("consumer.key");
            String consumerSecret = prop.getProperty("consumer.secret");
            String accessToken = prop.getProperty("access.token");
            String accessTokenSecret = prop.getProperty("access.token.secret");

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setOAuthConsumerKey(consumerKey)
                    .setOAuthConsumerSecret(consumerSecret)
                    .setOAuthAccessToken(accessToken)
                    .setOAuthAccessTokenSecret(accessTokenSecret);
            return cb.build();
        } catch (IOException e) {
            throw new IOException();
        }
    }
}

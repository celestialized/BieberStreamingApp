package com.interview.bieber.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class ApplicationConfig {
    @Value("${message.track}")
    private String messageTrack;

    @Value("${message.max.count}")
    private int maxMessageCount;

    @Value("${message.streaming.max.duration}")
    private int maxStreamingDuration;

    @Value("${twitter.consumer.key}")
    private String consumerKey;

    @Value("${twitter.consumer.secret}")
    private String consumerSecret;

    @Value("${message.tag}")
    private String tag;

    public String getMessageTrack() {
        return messageTrack;
    }

    public int getMaxMessageCount() {
        return maxMessageCount;
    }

    public int getMaxStreamingDuration() {
        return maxStreamingDuration;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getTag() {
        return tag;
    }
}

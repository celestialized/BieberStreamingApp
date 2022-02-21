package com.interview.bieber;

import com.interview.bieber.twitter.ops.TwitterService;
import com.interview.bieber.twitter.ops.TwitterStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@SpringBootApplication
public class BieberApplication implements CommandLineRunner {

    private final TwitterService twitterService;

    private final TwitterStreamer twitterStreamer;


    private final Environment environment;

	private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamer.class);

    @Autowired
    public BieberApplication(TwitterService twitterService, TwitterStreamer twitterStreamer, Environment environment) {
        this.twitterService = twitterService;
        this.twitterStreamer = twitterStreamer;
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(BieberApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // authorize by a user, get pin and receive accesstoken
        twitterService.setAccessToken();
        // streaming
        twitterStreamer.startStreaming();
    }
}

@Component
class BeanService {
    @Bean
    public Executor executor() {
        ThreadFactory factory = new CustomizableThreadFactory("bieber-pool-");
        return Executors.newFixedThreadPool(1, factory);
    }
}
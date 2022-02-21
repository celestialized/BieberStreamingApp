package com.interview.bieber.twitter.ops;

import com.interview.bieber.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;
import java.util.Scanner;

@Service
public class TwitterService {

	private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

	private Twitter twitter;

	private AccessToken accessToken;

	private final ApplicationConfig applicationConfig;

	@Autowired
	public TwitterService(ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	@PostConstruct
	public void init() {
		Assert.hasText(this.applicationConfig.getConsumerKey(), "Configuration issue: consumerKey can not be empty");
		Assert.hasText(this.applicationConfig.getConsumerSecret(), "Configuration issue: consumerSecret can not be empty");
		logger.info("{} initialized", getClass().getName());
	}

	public void setAccessToken() throws TwitterException {
		// Get the root twitter object
		createTwitter();

		RequestToken requestToken = twitter.getOAuthRequestToken();
		String pin = readPIN(requestToken);

		accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		twitter.setOAuthAccessToken(accessToken);

		logger.info("Received Token key: {} secret: {}", accessToken.getToken(), accessToken.getTokenSecret());
	}

	private void createTwitter() {
		// This method for unit test
		if (twitter != null) {
			return;
		}

		// Get the root twitter object
		twitter = TwitterFactory.getSingleton();
		// Set up the access tokens and keys to get permission to access
		twitter.setOAuthConsumer(this.applicationConfig.getConsumerKey(), this.applicationConfig.getConsumerSecret());
	}

	private String readPIN(RequestToken requestToken) {
		String pin = null;
		try (Scanner scanner = new Scanner(System.in)) {
			logger.info("\nGo to the following link in your browser:\n{}\n", requestToken.getAuthorizationURL());
			logger.info("\nPlease enter the retrieved PIN:");
			pin = scanner.next();
		}
		Assert.notNull(pin, "Unable to read entered PIN");
		return pin;
	}

	public TwitterStream createTwitterStream(StatusListener statusListener) {
		// Validations
		Assert.notNull(statusListener, "statusListener can not be null");
		Assert.notNull(accessToken, "accessToken can not be null");

		// Twitter Stream configuration
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(this.applicationConfig.getConsumerKey()).setOAuthConsumerSecret(this.applicationConfig.getConsumerSecret())
				.setOAuthAccessToken(accessToken.getToken()).setOAuthAccessTokenSecret(accessToken.getTokenSecret());

		TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
		twitterStream.addListener(statusListener);

		return twitterStream;
	}
}

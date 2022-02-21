/**
 *
 */
package com.interview.bieber.twitter.ops;

import com.interview.bieber.config.ApplicationConfig;
import com.interview.bieber.domain.Author;
import com.interview.bieber.domain.Message;
import com.interview.bieber.storage.service.SaveTweetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import twitter4j.*;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TwitterStreamer {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamer.class);

	private final TwitterService twitterService;

	private final ApplicationConfig applicationConfig;

	private final Executor executor;

	private CountDownLatch latch;

	private StatusListener listener;

	private final SaveTweetService saveTweetService;

	@Autowired
	public TwitterStreamer(TwitterService twitterService, Executor executor, ApplicationConfig applicationConfig, SaveTweetService saveTweetService) {
		this.twitterService = twitterService;
		this.executor = executor;
		this.applicationConfig = applicationConfig;
		this.saveTweetService = saveTweetService;
	}

	@PostConstruct
	public void init() {
		Assert.hasText(this.applicationConfig.getMessageTrack(), "Configuration issue: messageTrack can not be empty");
		Assert.state(this.applicationConfig.getMaxMessageCount() > 0, "Configuration issue: maxMessageCount should be positive");
		Assert.state(this.applicationConfig.getMaxStreamingDuration() > 0, "Configuration issue: maxStreamingDuration should be positive");
		LOG.info("{} initialized", getClass().getName());
	}

	public long startStreaming() throws InterruptedException {
		createCountDownLatch();
		AtomicInteger counter = new AtomicInteger(0);
		StatusListener statusListener = getStatusListener(latch, counter);
		FilterQuery trackFilterQuery = createTrackerFilterQuery(this.applicationConfig.getMessageTrack(), this.applicationConfig.getTag());

		TwitterStream twitterStream = twitterService.createTwitterStream(statusListener);

		StopWatch sw = new StopWatch();
		sw.start();

		twitterStream.filter(trackFilterQuery);

		// wait maximum maxStreamingDuration seconds
		boolean stopped = latch.await(this.applicationConfig.getMaxStreamingDuration(), TimeUnit.SECONDS);
		sw.stop();

		destroyTwitterStream(twitterStream, statusListener);
		LOG.info("Total number of tweets processed: {}", this.saveTweetService.getNumberOfTweetsProcessed());
		LOG.info("Total number of unique tweets: {}", this.saveTweetService.getNumberOfTweets());
		LOG.info("Total number of authors: {}", this.saveTweetService.getNumberOFAuthors());

		LOG.info("twitterStream destroyed:{} elapsed time: {}", stopped, sw.getTotalTimeMillis());

		return sw.getTotalTimeMillis();
	}

	public void createCountDownLatch() {
		if (latch != null) {
			return;
		}
		latch = new CountDownLatch(this.applicationConfig.getMaxMessageCount());
	}

	private FilterQuery createTrackerFilterQuery(String... filters) {
		FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.track(filters);

		return tweetFilterQuery;
	}

	private void destroyTwitterStream(TwitterStream twitterStream, StatusListener statusListener) {
		LOG.info("Wait for destroying twitterStream");
		twitterStream.removeListener(statusListener);
		twitterStream.shutdown();
	}

	public StatusListener getStatusListener(CountDownLatch latch, AtomicInteger counter) {
		if (listener != null) {
			return listener;
		}

		return new StatusListener() {

			public void onStatus(Status status) {
				LOG.info("StatusListener onStatus");
				latch.countDown();

				// maybe stream not stopped yet
				if (counter.incrementAndGet() <= applicationConfig.getMaxMessageCount()) {
					executor.execute(() -> {
						Author author = new Author(status.getUser().getId(), status.getUser().getCreatedAt().getTime(),
							status.getUser().getName(), status.getUser().getScreenName());
						Message message = new Message(status.getId(), status.getCreatedAt().getTime(), status.getText(), author);

						saveTweetService.saveTweet(author, message);
					});
				}
			}

			@Override
			public void onException(Exception ex) {
				LOG.error("StatusListener onException: ", ex);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				LOG.info("StatusListener onDeletionNotice");
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				LOG.info("StatusListener onTrackLimitationNotice");
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				LOG.info("StatusListener onScrubGeo");
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				LOG.info("StatusListener onStallWarning");
			}
		};
	}
}

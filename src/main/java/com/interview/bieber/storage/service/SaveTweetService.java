package com.interview.bieber.storage.service;

import com.interview.bieber.domain.Author;
import com.interview.bieber.domain.Message;

public interface SaveTweetService {
    void saveTweet(Author author, Message message);
    Long getNumberOFAuthors();
    Integer getNumberOfTweetsProcessed();
    Long getNumberOfTweets();
}

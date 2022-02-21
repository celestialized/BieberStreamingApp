package com.interview.bieber.storage.serviceImpl;

import com.interview.bieber.storage.repository.AuthorRepository;
import com.interview.bieber.storage.repository.MessageRepository;
import com.interview.bieber.domain.Author;
import com.interview.bieber.domain.Message;
import com.interview.bieber.storage.service.SaveTweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveTweetServiceImpl implements SaveTweetService {
    private Integer numberOfTweetsProcessed = 0;

    private final AuthorRepository authorRepository;

    private final MessageRepository messageRepository;

    @Autowired
    public SaveTweetServiceImpl(AuthorRepository authorRepository, MessageRepository messageRepository) {
        this.authorRepository = authorRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void saveTweet(Author author, Message message) {
        numberOfTweetsProcessed += 1;
        if (!this.authorRepository.existsById(author.getUserId())) {
            this.authorRepository.save(author);
        }
        if (!this.messageRepository.existsById(message.getMessageId())) {
            this.messageRepository.save(message);
        }
    }

    @Override
    public Long getNumberOFAuthors() {
        return this.authorRepository.count();
    }

    @Override
    public Integer getNumberOfTweetsProcessed() {
        return numberOfTweetsProcessed;
    }

    @Override
    public Long getNumberOfTweets() {
        return this.messageRepository.count();
    }
}

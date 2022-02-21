package com.interview.bieber.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @Column(name = "message_id")
    private Long messageId;
    private Long creationDate;
    private String text;

    @ManyToOne
    private Author author;

    public Message() {
    }

    public Message(Long messageId, Long creationDate, String text, Author author) {
        super();
        this.messageId = messageId;
        this.creationDate = creationDate;
        this.text = text;
        this.author = author;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!Objects.equals(messageId, message.messageId)) return false;
        if (!Objects.equals(creationDate, message.creationDate))
            return false;
        if (!Objects.equals(text, message.text)) return false;
        return Objects.equals(author, message.author);
    }

    @Override
    public int hashCode() {
        int result = messageId != null ? messageId.hashCode() : 0;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
            "messageId=" + messageId +
            ", creationDate=" + creationDate +
            ", text='" + text + '\'' +
            ", author=" + author +
            '}';
    }
}

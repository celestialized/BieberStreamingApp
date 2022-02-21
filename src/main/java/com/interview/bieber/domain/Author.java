package com.interview.bieber.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Author {
    @Id
    @Column(name = "user_id")
    private Long userId;
    private Long creationDate;
    private String userName;
    private String screenName;

    public Author() {
    }

    public Author(Long userId, Long creationDate, String userName, String screenName) {
        super();
        this.userId = userId;
        this.creationDate = creationDate;
        this.userName = userName;
        this.screenName = screenName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        if (!Objects.equals(userId, author.userId)) return false;
        if (!Objects.equals(creationDate, author.creationDate))
            return false;
        if (!Objects.equals(userName, author.userName)) return false;
        return Objects.equals(screenName, author.screenName);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (screenName != null ? screenName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Author{" +
            "userId=" + userId +
            ", creationDate=" + creationDate +
            ", userName='" + userName + '\'' +
            ", screenName='" + screenName + '\'' +
            '}';
    }
}

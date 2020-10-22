package com.example.bookmarkauth.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * This is bookmark model class
 */
@Component
public class Bookmark {

    @NotBlank(message = "Link must be filled")
    private String link;
    private String type;

    @NotBlank(message = "Name must be filled")
    private String name;
    private long userId;

    private long bookmarkId;

    public long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

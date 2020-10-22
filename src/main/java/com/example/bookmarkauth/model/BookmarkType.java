package com.example.bookmarkauth.model;

/**
 * This is bookmark type enum
 */
public enum BookmarkType {

    PUBLIC("public"), PRIVATE("private");

    String value;

    BookmarkType(String value){
        this.value = value;
    }

    /**
     * This method is used for returning bookmark value
     *
     * @return value
     */
    public String getValue(){
        return value;
    }
}

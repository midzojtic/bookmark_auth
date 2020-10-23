package com.example.bookmarkauth.dao;


import com.example.bookmarkauth.model.Bookmark;

import java.util.List;

public interface BookmarkDao {

    List<Bookmark> getAllPublicBookmarks();

    long insertBookmark(Bookmark bookmark);

    long insertBookmarkBookmarkType(long bookmarkId, String bookmarkType);

    long insertUserBookmark(long userId, long bookmarkId);

    List<Bookmark> getAllPrivateBookmarks(long userId);

    Bookmark getBookmark(String name, long userId);

    long deleteFromBookmark(long bookmarkId);

    long updateBoookmark(Bookmark bookmarkOld, Bookmark bookmarkNew);

    long deleteFromUserBookmark(long bookmarkId);

    long deleteFromBookmarkBookmar(long bookmarkId);



}

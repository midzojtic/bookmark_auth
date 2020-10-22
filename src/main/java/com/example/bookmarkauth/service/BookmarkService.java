package com.example.bookmarkauth.service;

import com.example.bookmarkauth.dao.BookmarkDaoImpl;
import com.example.bookmarkauth.model.Bookmark;
import com.example.bookmarkauth.model.BookmarkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This is bookmark bussiness service class
 */
@Service
public class BookmarkService {

    private static final Logger LOG = LoggerFactory.getLogger(BookmarkService.class);
    private final BookmarkDaoImpl bookmarkDaoImpl;

    @Autowired
    public BookmarkService(BookmarkDaoImpl bookmarkDaoImpl){
        this.bookmarkDaoImpl = bookmarkDaoImpl;
    }

    /**
     * Method is used for adding private bookmark
     *
     * @param bookmark
     * @return result success
     */
    public boolean addPrivateBookmark(Bookmark bookmark){
        bookmark.setType(BookmarkType.PRIVATE.getValue());
        return addBookmark(bookmark);
    }

    /**
     * Method is used for adding public bookmark
     * @param bookmark
     * @return result success
     */
    public boolean addPublicBookmark(Bookmark bookmark){
        bookmark.setType(BookmarkType.PUBLIC.getValue());
        return addBookmark(bookmark);
    }

    /**
     * Method is used for adding bookmark
     *
     * @param bookmark
     * @return result success
     */
    public boolean addBookmark(Bookmark bookmark){

        LOG.debug("BookmarkService.addBookmark");
        long bookmarkId = bookmarkDaoImpl.insertBookmark(bookmark);

        bookmarkDaoImpl.insertBookmarkBookmarkType(bookmarkId, bookmark.getType());
        bookmarkDaoImpl.insertUserBookmark(bookmark.getUserId(),bookmarkId);

        return true;

    }

    /**
     * This method returns all public bookmarks
     *
     * @return list
     */
    public List<Bookmark> getAllPublicBookmarks() {
        LOG.debug("Accessed BookmarkService.getAllPublicBookmarks");
        return bookmarkDaoImpl.getAllPublicBookmarks();
    }

    /**
     * This method returns all public and private bookmarks
     * @param userId
     * @return list
     */
    public List<Bookmark> getAllPublicAndPrivateBookmarks(long userId) {
        LOG.debug("Accessed BookmarkService.getAllPublicAndPrivateBookmarks");
        return bookmarkDaoImpl.getAllPublicAndPrivateBookmarsForUser(userId);
    }

    /**
     * This method deletes from bookmark
     * @param name
     * @param userId
     * @return success
     */
    public boolean deleteBookmark(String name, long userId){

        Bookmark bookmark = bookmarkDaoImpl.getBookmark(name, userId);

        if(bookmark == null){
            LOG.debug("Bookmark is deleted or user is not owner of bookmark");
            return false;
        }

        long bookmarkId = bookmark.getBookmarkId();

        bookmarkDaoImpl.deleteFromBookmark(bookmarkId);
        bookmarkDaoImpl.deleteFromBookmarkBookmar(bookmarkId);
        bookmarkDaoImpl.deleteFromUserBookmark(bookmarkId);

        return true;

    }

    /**
     * This method updates bookmark
     *
     * @param bookmarkOld
     * @param bookmarkNew
     * @param userId
     * @return success
     */
    public boolean updateBookmark(Bookmark bookmarkOld, Bookmark bookmarkNew, long userId){

        Bookmark bookmarkDB = bookmarkDaoImpl.getBookmark(bookmarkOld.getName(), userId);

        if(bookmarkDB == null){
            LOG.debug("User is not owner of bookmark");
            return false;
        }

        bookmarkNew.setBookmarkId(bookmarkDB.getBookmarkId());

        LOG.trace("Bookmark: {}", bookmarkNew);

        bookmarkDaoImpl.updateBoookmark(bookmarkNew);

        return true;

    }





}

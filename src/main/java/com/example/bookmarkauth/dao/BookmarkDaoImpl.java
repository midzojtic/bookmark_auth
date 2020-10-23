package com.example.bookmarkauth.dao;

import com.example.bookmarkauth.model.Bookmark;
import com.example.bookmarkauth.model.BookmarkType;
import com.example.bookmarkauth.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository @Transactional
public class BookmarkDaoImpl implements BookmarkDao {

    private static final Logger LOG = LoggerFactory.getLogger(BookmarkDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_INTO_BOOKMARK = "INSERT INTO bookmark( link, name ) values ( ?, ? ) ";
    private static final String INSERT_INTO_BOOKMARK_BOOKMARK_TYPE = " INSERT INTO bookmark_bookmark_type (bookmark_id, book_type_id ) " +
            " SELECT ? , book_type_id FROM bookmark_type WHERE code = ? ";
    private static final String INTSERT_INTO_USER_BOOKMARK = " INSERT INTO users_bookmark (bookmark_id, user_id ) values (? , ?) ";
    private static final String GET_FROM_BOOKMARK_FOR_USER = "SELECT b.bookmark_id, link, b.name, code FROM bookmark b JOIN users_bookmark ub ON b.bookmark_id = ub.bookmark_id" +
            " JOIN bookmark_bookmark_type bbt ON b.bookmark_id = bbt.bookmark_id JOIN bookmark_type bt ON bbt.book_type_id = bt.book_type_id WHERE user_id = ? AND TRIM(b.name) = ? ";
    private static final String DELETE_FROM_BOOKMARK = "DELETE FROM BOOKMARK WHERE bookmark_id = ? ";
    private static final String UPDATE_FROM_BOOKMARK = "UPDATE BOOKMARK SET = CASE WHEN link <> ? THEN ? ELSE END, name = CASE WHEN name <> ? THEN ? ELSE name END WHERE bookmark_id = ? ";
    private static final String DELETE_FROM_USER_BOOKMARK = "DELETE FROM USERS_BOOKMARK WHERE bookmark_id = ? ";
    private static final String DELETE_FROM_BOOKMARK_BOOKMARK_TYPE = "DELETE FROM BOOKMARK_BOOKMARK_TYPE WHERE bookmark_id = ? ";


    @Autowired
    public BookmarkDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    /**
     * Method fetches all public bookmarks
     *
     * @return list
     */
    @Override
    public List<Bookmark> getAllPublicBookmarks() {

        StringBuilder sb = createSQLForFetchingBookmarks();
        sb.append("WHERE bt.code = ? ");

        String sql = sb.toString();

        LOG.debug("SQL: {}", sql);

        List<Bookmark> list = null;

        try {

            list = jdbcTemplate.query(sb.toString(), new Object[]{BookmarkType.PUBLIC.getValue()}, new BookmarkRowMapper());

        } catch (EmptyResultDataAccessException e){
            //ignored if null rows
            LOG.debug("Nothing found for public bookmarks");
        }

        LOG.trace("Bookmark list {}", list);

        return list;
    }

    /**
     * Method is used for inserting into bookmark
     * @param bookmark
     * @return generated bookmark id
     */
    @Override
    public long insertBookmark(Bookmark bookmark) {

        LOG.debug("BookmarkDaoImpl.insertBookmark");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_INTO_BOOKMARK, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bookmark.getLink().trim());
            ps.setString(2, bookmark.getName().trim());

            return ps;
        }, keyHolder);

        return Long.parseLong(keyHolder.getKeys().get("bookmark_id").toString());
    }

    /**
     * Method is used for inserting into bookmark bookmark type
     * @param bookmarkId
     * @param bookmarkType
     * @return long
     */
    @Override
    public long insertBookmarkBookmarkType(long bookmarkId, String bookmarkType) {
        LOG.debug("BookmarkDaoImpl.insertBookmarkBookmarkType");
        return jdbcTemplate.update(INSERT_INTO_BOOKMARK_BOOKMARK_TYPE, bookmarkId, bookmarkType);
    }

    /**
     * Method is used for inserting into user bookmark
     *
     * @param userId
     * @param bookmarkId
     * @return long
     */
    @Override
    public long insertUserBookmark(long userId, long bookmarkId) {
        LOG.debug("BookmarkDaoImpl.insertUserBookmark");
        return jdbcTemplate.update(INTSERT_INTO_USER_BOOKMARK,bookmarkId,userId);
    }

    /**
     * Method fetches all private and public bookmarks for user
     *
     * @param userId
     * @return list
     */
    @Override
    public List<Bookmark> getAllPrivateBookmarks(long userId) {

        LOG.debug("BookmarkDaoImpl.getAllPublicAndPrivateBookmarsForUser");
        StringBuilder sb = createSQLForFetchingBookmarks();
        sb.append("LEFT JOIN users_bookmark ub ON ub.bookmark_id = b.bookmark_id ");
        sb.append("WHERE ");
        sb.append("code = ? ");
        sb.append("AND ");
        sb.append("user_id = ? ");

        String sql = sb.toString();

        LOG.debug("SQL: {}", sql);

        List<Bookmark> list = null;

        try {

            list = jdbcTemplate.query(sb.toString(), new Object[]{BookmarkType.PRIVATE.getValue(), userId}, new BookmarkRowMapper());

        } catch (EmptyResultDataAccessException e){
            //ignored if null rows
            LOG.debug("Nothing found for public bookmarks");
        }

        LOG.trace("Bookmark list {}", list);
        return list;
    }

    /**
     * Method fetches bookmark by name and ownerId
     *
     * @param name
     * @param userId
     * @return bookmark
     */
    @Override
    public Bookmark getBookmark(String name, long userId) {

        LOG.trace("Accessed BookmarkDaoImpl.getBookmark: {}, {}", name, userId);

        Bookmark bookmark = null;

        try {
            bookmark = jdbcTemplate.queryForObject(GET_FROM_BOOKMARK_FOR_USER, new Object[]{userId, name.trim()}, new BookmarkRowMapper());
        } catch (EmptyResultDataAccessException e) {
            //ignored, expected when checking user
            LOG.debug("Nothing found for getting bookmark");
        }

        LOG.trace("Bookmark: {}", bookmark);

        return bookmark;
    }

    /**
     * This method deletes from bookmark
     *
     * @param bookmarkId
     * @return long
     */
    @Override
    public long deleteFromBookmark(long bookmarkId) {
        LOG.debug("BookmarkDaoImpl.deleteFromBookmark");
        return jdbcTemplate.update(DELETE_FROM_BOOKMARK, bookmarkId);
    }

    /**
     * This method updates bookmark
     *
     * @param bookmarkOld
     * @param bookmarkNew
     * @return long
     */
    @Override
    public long updateBoookmark(Bookmark bookmarkOld, Bookmark bookmarkNew) {

        LOG.debug("BookmarkDaoImpl.updateBoookmark");

        StringBuilder sb = new StringBuilder("UPDATE bookmark ");

        long l = 0;

        if(!bookmarkOld.getName().equals(bookmarkNew.getName()) && bookmarkOld.getLink().equals(bookmarkOld.getLink())){
            sb.append("set name = ? where bookmark_id = ? ");
            l = jdbcTemplate.update(sb.toString(), new Object[]{bookmarkNew.getName(), bookmarkNew.getBookmarkId()});
        } else if (!bookmarkOld.getName().equals(bookmarkNew.getName()) && !bookmarkOld.getLink().equals(bookmarkOld.getLink())){
            sb.append("set name = ?, link = ? where bookmark_id = ? ");
            l = jdbcTemplate.update(sb.toString(), new Object[]{bookmarkNew.getName(), bookmarkNew.getLink(), bookmarkNew.getBookmarkId()});
        } else if (bookmarkOld.getName().equals(bookmarkNew.getName()) && !bookmarkOld.getLink().equals(bookmarkOld.getLink())) {
            sb.append("set link = ? where bookmark_id = ? ");
            l = jdbcTemplate.update(sb.toString(), new Object[]{bookmarkNew.getLink(), bookmarkNew.getBookmarkId()});

        }
        return l;



    }

    @Override
    public long deleteFromUserBookmark(long bookmarkId) {
        LOG.debug("BookmarkDaoImpl.insertUserBookmark");
        return jdbcTemplate.update(DELETE_FROM_USER_BOOKMARK, bookmarkId);
    }

    @Override
    public long deleteFromBookmarkBookmar(long bookmarkId) {
        LOG.debug("BookmarkDaoImpl.insertUserBookmark");
        return jdbcTemplate.update(DELETE_FROM_BOOKMARK_BOOKMARK_TYPE, bookmarkId);
    }


    private StringBuilder createSQLForFetchingBookmarks(){

        StringBuilder sb = new StringBuilder("SELECT b.bookmark_id, link, name, code ");
        sb.append("FROM bookmark b ");
        sb.append("JOIN bookmark_bookmark_type bbt ON b.bookmark_id = bbt.bookmark_id ");
        sb.append("JOIN bookmark_type bt ON bt.book_type_id = bbt.book_type_id ");

        return sb;
    }
}

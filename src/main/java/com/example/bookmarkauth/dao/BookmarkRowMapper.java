package com.example.bookmarkauth.dao;

import com.example.bookmarkauth.model.Bookmark;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookmarkRowMapper implements RowMapper<Bookmark> {

    @Override
    public Bookmark mapRow(ResultSet resultSet, int i) throws SQLException {

            Bookmark bookmark = new Bookmark();

            bookmark.setLink(resultSet.getString("link"));
            bookmark.setName(resultSet.getString("name"));
            bookmark.setType(resultSet.getString("code"));
            bookmark.setBookmarkId(resultSet.getInt("bookmark_id"));

        return bookmark;
    }
}

package com.example.bookmarkauth.dao;

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

/**
 * This is user dao repository implementation
 */
@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String INSERT_INTO_USER = "INSERT INTO users(username, password) values (? , ?)";
    private static final String SELECT_FROM_USER = "SELECT user_id, username, password FROM users WHERE username = ? ";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long insertUser(User user) {

        LOG.debug("Accessed UserDaoImpl.insertUser");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_INTO_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            return ps;
        }, keyHolder);

        return Long.parseLong(keyHolder.getKeys().get("user_id").toString());
    }

    @Override
    public User getUserByUsername(String username) {

        LOG.trace("Accessed UserDaoImpl.getUserByUsername: {}", username);

        User user = null;

        try {
            user = jdbcTemplate.queryForObject(SELECT_FROM_USER, new Object[]{username.trim()}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            //ignored, expected when checking user
            LOG.debug("Nothing found for getting user");
        }


        return user;
    }
}

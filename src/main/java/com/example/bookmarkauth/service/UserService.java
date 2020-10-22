package com.example.bookmarkauth.service;

import com.example.bookmarkauth.dao.UserDaoImpl;
import com.example.bookmarkauth.model.User;
import com.example.bookmarkauth.model.UserTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This is user service
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserDaoImpl userDaoImpl;

    @Autowired
    private PasswordEncoder encoder;


    @Autowired
    public UserService(UserDaoImpl userDaoImpl) {
        this.userDaoImpl = userDaoImpl;
    }

    /**
     * This method is used for inserting public user
     *
     * @param user
     * @return boolean success or fail
     */
    public boolean insertPublicUser(User user){

        LOG.debug("Accessed insertPublicUser");
        user.setUserType(UserTypeEnum.PUBLIC.getValue());
        return insertUser(user);
    }

    /**
     * This method is used for inserting private user
     *
     * @param user
     * @return boolean success or fail
     */
    public boolean insertPrivateUser(User user){

        LOG.debug("Accessed insertPrivateUser");
        user.setUserType(UserTypeEnum.PRIVATE.getValue());
        return insertUser(user);
    }

    /**
     * This method is used for inserting new user
     *
     * @param user
     * @return user
     */
    public boolean insertUser(User user) {

        LOG.debug("Accessed UserService.insertUser");

        User userDB = userDaoImpl.getUserByUsername(user.getUsername().trim());

        if(userDB != null){
            LOG.debug("User already exists");
            return false;
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userDaoImpl.insertUserUserType(userDaoImpl.insertUser(user), user.getUserType());

        return true;
    }

    /**
     * This is method which fetches user
     *
     * @param username
     * @return user
     */
    public User getUserByUsername(String username) {
        LOG.debug("Accessed UserService.getUserByUsername");
        return userDaoImpl.getUserByUsername(username.trim());
    }

    /**
     * This method is used for login and checking password
     *
     * @param user
     * @return
     */
    public boolean loginUser(User user){

        LOG.debug("Accessed loginUser");

        User userDB = userDaoImpl.getUserByUsername(user.getUsername().trim());

        LOG.trace("user from database: {}", userDB);

        String encryptedPassword = userDB.getPassword();

        LOG.trace("encryptedPassword: {}", encryptedPassword);
        LOG.trace("password: {}", user.getPassword());

        return passwordMatching(user.getPassword(), encryptedPassword);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean passwordMatching(String password, String encryptedPassword){
        return encoder.matches(password, encryptedPassword);
    }



}

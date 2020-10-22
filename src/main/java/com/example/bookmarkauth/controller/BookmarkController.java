package com.example.bookmarkauth.controller;

import com.example.bookmarkauth.model.Bookmark;
import com.example.bookmarkauth.model.RestDto;
import com.example.bookmarkauth.model.User;
import com.example.bookmarkauth.service.BookmarkService;
import com.example.bookmarkauth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookmark/")
@Validated
public class BookmarkController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final BookmarkService bookmarkService;
    private final UserService userService;

    @Autowired
    private ObjectFactory<HttpSession> httpSessionObjectFactory;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService, UserService userService) {
        this.bookmarkService = bookmarkService;
        this.userService = userService;
    }

    /**
     * This method is used for listing all public bookmarks for public user with message
     *
     * @return list of public bookmarks
     */
    @GetMapping("getPublicBookmarks")
    public RestDto<List<Bookmark>> getPublicBookmarks() {

        LOG.info("Accessed getPublicBookmarks");

        Optional<String> o = getUsername();

        if (!o.isPresent())
            return RestDto.fail("User is not logged in");


        List<Bookmark> list = bookmarkService.getAllPublicBookmarks();

        LOG.trace("Public bookmarks: {}", list);

        return RestDto.success(list, "Successfully loaded public bookmarks");

    }

    /**
     * This method is used for adding public bookmark
     *
     * @param bookmark
     * @return message success
     */
    @PostMapping("addPublicBookmark")
    public @ResponseBody
    RestDto<String> addPublicBookmark(@RequestBody @Valid Bookmark bookmark) {

        LOG.info("Accessed addPublicBookmark");

        Optional<String> o = getUsername();

        if (!o.isPresent())
            return RestDto.fail("User is not logged in");

        String username = o.get();

        User user = userService.getUserByUsername(username);

        if (user == null) {
            LOG.info("User does not exist");
            return RestDto.fail("User does not exist: " + username);
        }

        bookmark.setUserId(user.getId());
        LOG.trace("Bookmark => {}", bookmark);

        return bookmarkService.addPublicBookmark(bookmark) ? RestDto.success("Successfully added public bookmark")
                : RestDto.fail("Error while trying to add public bookmark");

    }


    /**
     * This method is used for adding private bookmark
     *
     * @param bookmark
     * @return message success
     */
    @PostMapping("addPrivateBookmark")
    public @ResponseBody
    RestDto<String> addPrivateBookmark(@RequestBody @Valid Bookmark bookmark) {

        LOG.info("Accessed addPrivateBookmark");

        Optional<String> o = getUsername();

        if (!o.isPresent())
            return RestDto.fail("User is not logged in");

        String username = o.get();

        User user = userService.getUserByUsername(username);

        if (user == null) {
            LOG.info("User does not exist");
            return RestDto.fail("User does not exist: " + username);
        }
        bookmark.setUserId(user.getId());
        LOG.trace("Bookmark => {}", bookmark);

        return bookmarkService.addPrivateBookmark(bookmark) ? RestDto.success("Successfully added private bookmark")
                : RestDto.fail("Error while trying to add private bookmark");
    }

    /**
     * This method is used for listing all public bookmarks for public user with message
     *
     * @return list of public bookmarks
     */
    @GetMapping("getPrivateBookmarks")
    public RestDto<List<Bookmark>> getPrivateBookmarks() {

        LOG.info("Accessed getPrivateBookmarks");

        Optional<String> o = getUsername();

        if (!o.isPresent())
            return RestDto.fail("User is not logged in");

        String username = o.get();

        User user = userService.getUserByUsername(username);

        if (user == null) {
            LOG.info("User does not exist");
            return RestDto.fail("User does not exist: " + username);
        }

        List<Bookmark> list = bookmarkService.getAllPublicAndPrivateBookmarks(user.getId());

        LOG.trace("Public and private bookmarks: {}", list);

        return RestDto.success(list, "Successfully loaded public and private bookmarks");

    }

    /**
     * This method is used for updating bookmark
     *
     * @param bookmarkOld
     * @param bookmarkNew
     * @return message success
     */
    @PutMapping("updateBookmark")
    @ResponseBody
    public RestDto<String> updateBookmark(@RequestBody @Valid Bookmark bookmarkOld, @RequestBody @Valid Bookmark bookmarkNew) {

        LOG.info("Accessed BookmarkController.updateBookmark");

        LOG.trace("Old bookmark: {}", bookmarkOld);
        LOG.trace("New bookmark: {}", bookmarkNew);

        Optional<String> o = getUsername();

        if (!o.isPresent())
            return RestDto.fail("User is not logged in");

        String username = o.get();

        User user = userService.getUserByUsername(username);

        if (user == null) {
            LOG.info("User does not exist");
            return RestDto.fail("User does not exist: " + username);
        }

        LOG.trace("User: {}", user);

        return bookmarkService.updateBookmark(bookmarkOld, bookmarkNew, user.getId()) ? RestDto.success("Successfully updated bookmark")
                : RestDto.fail("Failed while trying to update bookmark");
    }

    /**
     * This method is used for deleting bookmark
     *
     * @param bookmark
     * @return message success
     */
    @DeleteMapping("deleteBookmark")
    @ResponseBody
    public RestDto<String> deleteBookmark(@RequestBody @Valid Bookmark bookmark) {

        LOG.info("Accessed BookmarkController.deleteBookmark");

        LOG.trace("Bookmark: {}", bookmark);

        Optional<String> o = getUsername();

        if (!o.isPresent())
            return RestDto.fail("User is not logged in");

        String username = o.get();

        User user = userService.getUserByUsername(username);

        if (user == null) {
            LOG.info("User does not exist");
            return RestDto.fail("User does not exist: " + username);
        }

        LOG.trace("User: {}", user);

        return bookmarkService.deleteBookmark(bookmark.getName(), user.getId()) ? RestDto.success("Bookmark successfully deleted")
                : RestDto.fail("Failed to delete bookmark");

    }


    private Optional<String> getUsername() {

        HttpSession session = httpSessionObjectFactory.getObject();

        Object obj = session.getAttribute("username");

        if (obj == null) {
            LOG.debug("User is not logged in");
            return Optional.empty();
        }

        return Optional.of(obj.toString());

    }


}

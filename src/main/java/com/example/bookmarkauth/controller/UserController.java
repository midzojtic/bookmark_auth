package com.example.bookmarkauth.controller;

import com.example.bookmarkauth.model.RestDto;
import com.example.bookmarkauth.model.User;
import com.example.bookmarkauth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * This is controller for user managment
 */
@RestController
@RequestMapping("/")
@Validated
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    private ObjectFactory<HttpSession> httpSessionObjectFactory;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * This method is used for registering new user
     *
     * @param user
     * @return success of fail message
     */
    @PostMapping("register")
    public @ResponseBody
    RestDto<String> register(@RequestBody @Valid User user) {

        LOG.info("Accessed BookmarkController.register");

        LOG.trace("user => {}", user);

        if (userService.getUserByUsername(user.getUsername()) != null) {
            return RestDto.fail("Public user already exisits");
        }

        return userService.insertUser(user) ? RestDto.success("Public user successfully added")
                : RestDto.fail("Error while adding public user");

    }

    /**
     * This method is used for login current user
     *
     * @param user
     * @return success or fail message
     */
    @PostMapping("login")
    public @ResponseBody
    RestDto<String> login(@RequestBody @Valid User user) {

        LOG.info("Accessed BookmarkController.login");

        LOG.trace("user => {}", user);

        HttpSession session = httpSessionObjectFactory.getObject();

        Object obj = session.getAttribute("username");

        if (obj != null && obj.toString().equals(user.getUsername())) {
            LOG.info("User already logged in");
            return RestDto.success("Already logged in");
        }

        boolean result = userService.loginUser(user);

        if (result) {
            session.setAttribute("username", user.getUsername());
            return RestDto.success("User successfully logged in");
        }

        return RestDto.fail("Username or password is incorrect");

    }

    @GetMapping("ping")
    public String ping() {
        return "It's alive";
    }

//    /**
//     * This is custom logout method, not needed to be implemented because of Spring
//     * Optional.
//     * @param request
//     * @param response
//     * @return
//     */
//    @PostMapping("logout")
//    public String logoutDo(HttpServletRequest request, HttpServletResponse response){
//        HttpSession session= request.getSession(false);
//        SecurityContextHolder.clearContext();
//        session= request.getSession(false);
//        if(session != null) {
//            session.invalidate();
//        }
//        for(Cookie cookie : request.getCookies()) {
//            cookie.setMaxAge(0);
//        }
//
//        return "logged out";
//    }


}

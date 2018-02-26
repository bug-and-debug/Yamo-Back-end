package com.locassa.yamo.controller;

import com.locassa.yamo.model.User;
import com.locassa.yamo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/pagination/library", method = RequestMethod.GET)
    public Page<User> pageUsers(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                @RequestParam(value = "count", defaultValue = "10", required = false) int count,
                                @RequestParam(value = "order", defaultValue = "DESC", required = false) Sort.Direction direction,
                                @RequestParam(value = "sort", defaultValue = "firstName", required = false) String sortProperty) {

        // 2. Find notifications.
        return userService.pageUsers(page, count, direction, sortProperty);

    }

    @RequestMapping(value = "/pagination/flags", method = RequestMethod.GET)
    public List<User> listUsers(@RequestParam(value = "timestamp") Long timestamp,
                                @RequestParam(value = "older") boolean older) {

        return userService.pageUsers(timestamp, older);

    }

}

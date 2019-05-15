/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.ui;

import com.ocularminds.eduzie.model.Post;
import com.ocularminds.eduzie.model.User;
import com.ocularminds.eduzie.service.Posts;
import com.ocularminds.eduzie.service.Users;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Director
 */
@Controller
@RequestMapping(value = {"/user/{username}"})
public class Timeline {

    final Users users;
    final Posts posts;

    @Autowired
    public Timeline(Users users, Posts posts) {
        this.users = users;
        this.posts = posts;
    }

    /*
	 * Displays a user's tweets.
     */
    @GetMapping(value = {"/timeline"})
    public ModelAndView read(@PathVariable(name = "longitude", required = true) Long userId) {
        User profileUser = users.get(userId);
        User authUser = null;//getAuthenticatedUser(req);
        boolean followed = false;
        if (authUser != null) {
            followed = users.isFollowing(authUser.getId(), profileUser.getId());
        }
        ModelAndView modelAndView = new ModelAndView();
        List<Post> messages = posts.findPostForUser(userId);
        modelAndView.addObject("pageTitle", profileUser.getName() + "'s Timeline");
        modelAndView.addObject("user", authUser);
        modelAndView.addObject("profileUser", profileUser);
        modelAndView.addObject("followed", followed);
        modelAndView.addObject("timelines", messages);
        modelAndView.setViewName("timeline");
        return modelAndView;
    }
}

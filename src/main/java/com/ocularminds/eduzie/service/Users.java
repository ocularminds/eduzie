/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.service;

import com.ocularminds.eduzie.model.User;

/**
 *
 * @author Festus Jejelowo
 */
public interface Users {

    public User get(Long userId);

    public User findByUserName(String email);

    public void follow(Long followerId, Long followeeId);

    public void unfollow(Long followerid, Long followeeid);

    public boolean isFollowing(Long followerId, Long followeeId);

    public void save(User usr) throws Exception;
}

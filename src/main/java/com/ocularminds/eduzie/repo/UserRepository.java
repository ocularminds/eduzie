/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.repo;

import com.ocularminds.eduzie.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Director
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    
    @Query("select u from User u where u.id = ?1 and u.follower.id in(?2)")
    List<User> followers(Long followerId, Long followeeId);
}

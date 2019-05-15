package com.ocularminds.eduzie.service;

import com.ocularminds.eduzie.Fault;
import com.ocularminds.eduzie.model.User;
import com.ocularminds.eduzie.common.Passwords;
import com.ocularminds.eduzie.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersImpl implements Users {

    final UserRepository repository;
    final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public UsersImpl(UserRepository repository, BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.repository = repository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    public Fault checkUser(User user) {

        Fault fault = new Fault();
        User userFound = findByUserName(user.getEmail());
        if (userFound == null) {
            fault.setError("Invalid username");
        } else if (!new Passwords(bcryptPasswordEncoder, user.getPassword()).verify(userFound.getPassword())) {
            fault.setError("Invalid password");
        } else {
            fault.setData(userFound);
        }

        return fault;
    }

    @Override
    public User get(Long userId) {
        return repository.getOne(userId);
    }

    @Override
    public User findByUserName(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void follow(Long followerId, Long followeeId) {
        User follower = get(followerId);
        User followee = get(followeeId);
        follower.follow(followee);
        repository.save(follower);
    }

    @Override
    public void unfollow(Long followerid, Long followeeid) {

        User follower = get(followerid);
        User followee = get(followeeid);
        follower.unFollow(followee);
        repository.save(follower);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followeeId) {
        return repository.followers(followerId, followeeId).size() > 0;
    }

    @Override
    public void save(User usr) throws Exception {
        User user;
        if ((usr.getId() == null) || (usr.getId() == 0)) {
            user = new User();
        } else {
            user = get(usr.getId());
        }

        user.setEmail(usr.getEmail());
        user.setPhone(usr.getPhone());
        user.setName(usr.getName());
        user.setPassword(usr.getPassword());
        user.setPic(usr.getPic());
        user.setAvatar(usr.getAvatar());
        user.setCoverImage(usr.getCoverImage());
        repository.save(user);
    }
}

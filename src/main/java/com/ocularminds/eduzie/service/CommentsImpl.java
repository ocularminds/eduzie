/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.service;

import com.ocularminds.eduzie.model.Comment;
import com.ocularminds.eduzie.repo.CommentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Festus Jejelowo
 */
public class CommentsImpl implements Comments {

    final CommentRepository repository;

    @Autowired
    public CommentsImpl(CommentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Comment get(Long commentId) {
        return repository.getOne(commentId);
    }

    @Override
    public List<Comment> comments(Long postId) {
        return repository.findAllByPostId(postId);
    }

    @Override
    public void delete(Long commentId) {
        repository.delete(get(commentId));
    }

}

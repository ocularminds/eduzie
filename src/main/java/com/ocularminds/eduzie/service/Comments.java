package com.ocularminds.eduzie.service;

import com.ocularminds.eduzie.model.Comment;
import java.util.List;

/**
 *
 * @author Festus Jejelowo
 */
public interface Comments {

    Comment get(Long commentId);

    List<Comment> comments(Long postId);
    
    void delete(Long commentId);
}

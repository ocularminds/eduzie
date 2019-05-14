
package com.ocularminds.eduzie.service;

import com.ocularminds.eduzie.model.Post;
import java.util.List;

/**
 *
 * @author Festus Jejelowo
 */
public interface Posts {

    void add(Post post);

    Post get(Long postId);

    List<Post> findByUserId(Long id);

    /**
     *
     * @param userId
     * @return
     */
    List<Post> findPostForUser(Long userId);

    /**
     *
     * @param id
     * @return
     */
    List<Post> findPublicTimelinePosts(Long id);
}

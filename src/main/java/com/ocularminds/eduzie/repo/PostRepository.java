/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.repo;

import com.ocularminds.eduzie.model.Post;
import com.ocularminds.eduzie.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Festus B Jejelowo
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByAuthorId(Long id);

    @Query(
            "select p from Post p where p.author.id = ?1 or p.author.id in("
            + "SELECT u.id FROM User u, IN (u.following) AS f WHERE f.id = ?2"
            + ") "
            + "order by t.published desc"
    )
    List<Post> findPostForUser(Long authorId, Long userId);

    List<Post> findByAuthorIdAndType(Long authorId, String timeline);
}

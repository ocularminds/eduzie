package com.ocularminds.eduzie.service;

import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

import com.ocularminds.eduzie.model.User;
import com.ocularminds.eduzie.model.Comment;
import com.ocularminds.eduzie.model.Post;
import com.ocularminds.eduzie.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostsImpl implements Posts {

    final SimpleDateFormat sdf = new SimpleDateFormat("DD MMM,yyyy hh:mm:ss", Locale.US);
    final PostRepository repository;

    @Autowired
    public PostsImpl(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> findByUser(Long userId) {
        return repository.findAllByAuthorId(userId);
    }

    @Override
    public List<Post> findPostForUser(Long userId) {
        return repository.findPostForUser(userId, userId);
    }

    @Override
    public List<Post> findPublicTimelinePosts(Long userId) {
        return repository.findByAuthorIdAndType(userId, "timeline");
    }

    /**
     *
     * @param post
     */
    @Override
    public void add(Post post) {
        User user = null;//em.find(User.class, m.getAuthor().getId());
        if ((post.getId() == null) || (post.getId() == 0)) {
            post.setPublished(new java.util.Date());
            repository.save(post);
        } else {
            Post oldPost = get(post.getId());
            oldPost.setText(post.getText());
            oldPost.setAuthor(user);
            oldPost.setType(post.getType());
            oldPost.setTitle(post.getTitle());
            oldPost.setPhoto(post.getPhoto());
            oldPost.setPlace(post.getPlace());
            oldPost.setTime(post.getTime());
            oldPost.setPublishedStr(sdf.format(new java.util.Date()));
            repository.save(oldPost);
        }
    }

    public void attend(Long postId, Long userid) throws Exception {
        //EntityManager em = DbFactory.instance().getConnection();
        User user = null;//em.find(User.class, userid);
        Post post = get(postId);
        post.addAttendee(user);
        repository.save(post);
    }

    public void comment(Long postId, Comment c, Long userid) throws Exception {
        User user = null;//em.find(User.class, userid);
        Post post = get(postId);
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setText(c.getText());
        comment.setPublishedStr(sdf.format(new java.util.Date()));

        post.addComment(comment);
        repository.save(post);
    }

    public void uncomment(String commentId) {
        //Query q = em.createQuery("delete from Comment c where c.id = ?1");
    }

    @Override
    public Post get(Long postId) {
        return repository.getOne(postId);
    }

    @Override
    public List<Post> findByUserId(Long id) {
        return repository.findAllByAuthorId(id);
    }
}

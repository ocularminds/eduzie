package com.ocularminds.eduzie.dao;

import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.ocularminds.eduzie.vao.User;
import com.ocularminds.eduzie.vao.Comment;
import com.ocularminds.eduzie.vao.Message;

public class PostWriter{

	private static PostWriter instance;
	final SimpleDateFormat sdf = new SimpleDateFormat("DD MMM,yyyy hh:mm:ss",Locale.US);

	public static PostWriter instance(){

		if(instance == null){
			instance = new PostWriter();
		}

		return instance;
	}

	private PostWriter(){
			//
	}

	public List<Message> findByUser(User user){

        String sql = "select t from Message t where t.author.id = ?1 order by message.pub_date desc";
        EntityManager em = DbFactory.instance().getConnection();
        Query q = em.createQuery(sql);
        q.setParameter(1,user.getId());
		List<Message> messages = q.getResultList();

		DbFactory.instance().close(em);
		return messages;
	}

	public List<Message> findPostForUser(User user){

        StringBuffer sb = new StringBuffer();
        sb.append("select t from Message t where t.author.id = ?1  ");
        sb.append("or t.author.id in(SELECT u.id FROM User u, IN (u.following) AS f WHERE f.id = ?2) ");
        sb.append("order by t.published desc ");

		EntityManager em = DbFactory.instance().getConnection();
		Query q = em.createQuery(sb.toString());
		q.setParameter(1,user.getId());
		q.setParameter(2,user.getId());

		return q.getResultList();
	}

	public List<Message> findPublicTimelineMessages(Long userid){

		StringBuffer sql = new StringBuffer();
		sql.append("select t from Message t where t.author.id = ?1 ");
		sql.append("and t.type = ?2 order by t.published desc");

		EntityManager em = DbFactory.instance().getConnection();
		Query q = em.createQuery(sql.toString());
		q.setParameter(1,userid);
		q.setParameter(2,"timeline");

		return q.getResultList();
	}

	public void write(Message m){

		EntityManager em = DbFactory.instance().getConnection();
		Message message;
		User user = em.find(User.class,m.getAuthor().getId());

		try {

			em.getTransaction().begin();
			if((m.getId() == null) || (m.getId().longValue() == 0)) {

				message = new Message();
				message.setPublished(new java.util.Date());

			} else {
				message = em.find(Message.class,m.getId());
			}

			message.setText(m.getText());
			message.setAuthor(user);
			message.setType(m.getType());
			message.setTitle(m.getTitle());
			message.setPhoto(m.getPhoto());
			message.setPlace(m.getPlace());
			message.setTime(m.getTime());
			message.setPublishedStr(sdf.format(new java.util.Date()));
			//message.setDescription(m.getDescription());

			if((user.getId() == null) || (user.getId().longValue() == 0)) {
				em.persist(message);
			} else {
				em.merge(message);
			}

			em.getTransaction().commit();

		}catch(Exception e){

		    em.getTransaction().rollback();
		    e.printStackTrace();

		}finally {

		  if(em.getTransaction().isActive()) em.getTransaction().rollback();
		  em.close();
		}
	}

	public void attend(String messageid,Long userid){

		EntityManager em = DbFactory.instance().getConnection();
		User user = em.find(User.class,userid);
		Message message = em.find(Message.class,new Long(messageid));

		try {

			em.getTransaction().begin();
			message.addAttendee(user);
			em.merge(message);

			em.getTransaction().commit();

		}catch(Exception e){

			em.getTransaction().rollback();
			e.printStackTrace();

		}finally {

		  if(em.getTransaction().isActive()) em.getTransaction().rollback();
		  em.close();
		}
	}

	public void comment(String messageid,Comment c,Long userid){

		EntityManager em = DbFactory.instance().getConnection();
		User user = em.find(User.class,userid);

		try {

			em.getTransaction().begin();
			Message message = em.find(Message.class,new Long(messageid));

			Comment comment = new Comment();
			comment.setAuthor(user);
			comment.setText(c.getText());
			comment.setPublishedStr(sdf.format(new java.util.Date()));

			message.addComment(comment);
			em.getTransaction().commit();

		}catch(Exception e){

			em.getTransaction().rollback();
			e.printStackTrace();

		}finally {

		  if(em.getTransaction().isActive()) em.getTransaction().rollback();
		  em.close();
		}
	}

	public void uncomment(String commentId){

		EntityManager em = DbFactory.instance().getConnection();
		try {

			Query q = em.createQuery("delete from Comment c where c.id = ?1");
            q.setParameter(1,new Long(commentId));
            q.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}finally {
		  em.close();
		}
	}
}
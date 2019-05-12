package com.ocularminds.eduzie.dao;

import java.util.List;
import com.ocularminds.eduzie.Fault;
import com.ocularminds.eduzie.vao.User;
import com.ocularminds.eduzie.common.Passwords;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Authorizer{

	private static Authorizer instance;

	public static Authorizer instance(){

		if(instance == null){
			instance = new Authorizer();
		}

		return instance;
	}

	private Authorizer(){
		//
	}

	public Fault checkUser(User user) {

		Fault fault = new Fault();
		User userFound = findByUserName(user.getEmail());
		if(userFound == null) {
			fault.setError("Invalid username");
		} else if(!Passwords.verifyPassword(user.getPassword(), userFound.getPassword())) {
			fault.setError("Invalid password");
		} else {
			fault.setData(userFound);
		}

		return fault;
	}

	public User findByUserName(String email){

        String sql = "select t from User t where t.email = ?1";
		EntityManager em = DbFactory.instance().getConnection();
		Query q = em.createQuery(sql);
		q.setParameter(1,email);
		List<User> users = q.getResultList();

		DbFactory.instance().close(em);
		return (users.size() > 0)?users.get(0):null;
	}

	public void follow(Long followerId, Long followeeId){

         EntityManager em = DbFactory.instance().getConnection();
		 try {

			  em.getTransaction().begin();

			  User follower = em.find(User.class,followerId);
			  User followee = em.find(User.class,followeeId);

			  follower.follow(followee);
			  em.merge(follower);
			  em.getTransaction().commit();

		  }catch(Exception e){
			  em.getTransaction().rollback();
		  }finally {

			  if(em.getTransaction().isActive()) em.getTransaction().rollback();
			  em.close();
		  }
	}

	public void unfollow(Long followerid, Long followeeid){

         EntityManager em = DbFactory.instance().getConnection();
         try {

			  em.getTransaction().begin();
			  User follower = em.find(User.class,followerid);
			  User followee = em.find(User.class,followeeid);

			  follower.unFollow(followee);
			  em.merge(follower);
			  em.getTransaction().commit();

		  }catch(Exception e){
			  em.getTransaction().rollback();
		  }finally {

			  if(em.getTransaction().isActive()) em.getTransaction().rollback();
			  em.close();
	      }
	}

	public boolean isFollowing(Long followerid, Long followeeid){

        EntityManager em = DbFactory.instance().getConnection();
        boolean following = false;
         try {

			 Query q = em.createQuery("select u from User u where u.id = ?1 and u.follower.id in(?2)");
			 q.setParameter(1,followeeid);
			 q.setParameter(2,followerid);

			 following = (q.getResultList().size() > 0)?true:false;

		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return following;
	}

	public void save(User usr)throws Exception{

		 EntityManager em = DbFactory.instance().getConnection();
         User user = null;

		  em.getTransaction().begin();
		  if((usr.getId() == null) || (usr.getId().longValue() == 0)) {
			 user = new User();

		  } else {
			 user = em.find(User.class,usr.getId());
		  }

		 user.setEmail(usr.getEmail());
		 user.setPhone(usr.getPhone());
		 user.setName(usr.getName());
		 user.setPassword(usr.getPassword());
		 user.setPic(usr.getPic());
		 user.setAvatar(usr.getAvatar());
		 user.setCoverImage(usr.getCoverImage());

		 if((usr.getId() == null) || (usr.getId().longValue() == 0)) {
			 em.persist(user);
		 } else {
			 em.merge(user);
		 }

		 em.getTransaction().commit();
		 em.close();
	}
}
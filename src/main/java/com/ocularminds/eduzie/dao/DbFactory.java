package com.ocularminds.eduzie.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.ocularminds.eduzie.vao.Feed;

public class DbFactory {

  private static final String PERSISTENCE_UNIT_NAME = "EduziPU";
  private static EntityManagerFactory factory;
  public static DbFactory instance;

  public static DbFactory instance() {

	  if(instance == null){
		  instance = new DbFactory();
	  }

	  return instance;
  }

  private DbFactory(){

	  Map<String, String> persistenceMap = new HashMap<String, String>();
	  try{

	      System.out.println("loading database properties");

	      URI dbUri = new URI(System.getenv("DATABASE_URL"));
		  String dbuser = dbUri.getUserInfo().split(":")[0];
		  String dbpass = dbUri.getUserInfo().split(":")[1];
		  String dburl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

		  persistenceMap.put("javax.persistence.jdbc.url",dburl);
		  persistenceMap.put("javax.persistence.jdbc.user",dbuser);
		  persistenceMap.put("javax.persistence.jdbc.password",dbpass);
		  //persistenceMap.put("javax.persistence.jdbc.driver", "<driver>");
		  factory = Persistence.createEntityManagerFactory(DbFactory.PERSISTENCE_UNIT_NAME, persistenceMap);
		  System.out.println("factory created "+factory);

	  }catch(Exception e){
		  e.printStackTrace();
	  }
  }

  public EntityManager getConnection(){
	  return factory.createEntityManager();
  }

  public void close(EntityManager em){
	  if(em != null){
		  em.close();
	  }
  }
}
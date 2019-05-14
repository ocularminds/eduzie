package com.ocularminds.eduzie.common;

import com.ocularminds.eduzie.model.Feed;
import com.ocularminds.eduzie.SearchObjectCache;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class FeedCache{

   private List<Feed> records;
   private static FeedCache cache;

   private FeedCache(){
	  records = new ArrayList<Feed>();
   }

   public static FeedCache instance(){

      if(cache == null){
      	 cache = new FeedCache();
      }

      return cache;
   }

   public List<Feed> findAll(){
	   return records;
   }

  /**
  * loads the json-encoded feed records file bundled with this application
  * and store them as in-memory database using List
  */
  public boolean load(List<SearchObjectCache> items){

   	   long id = 0;
   	   try{

   		   for(SearchObjectCache item:items){

   			 Feed feed = new Feed();

   			 feed.setId(new Long(id));
			 feed.setCategory(item.getCategory());
			 feed.setUrl(item.getUrl());
			 feed.setName(item.getTitle());
			 feed.setText(item.getText());
			// feed.setTime(item.getDate());

			 records.add(feed);
			 id++;

   		   }
   	   }catch(Exception e){
   		   e.printStackTrace();
   	   }

   	   return (id > 0)?true:false;
   }
}
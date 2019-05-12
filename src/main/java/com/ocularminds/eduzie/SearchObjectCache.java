package com.ocularminds.eduzie;

import java.time.LocalDateTime;
import com.ocularminds.eduzie.common.TextUtil;

public class SearchObjectCache implements java.io.Serializable{

  private int id;
  private String url;
  private String category;
  private String title;
  private String text;
  private String location;
  private String day;
  private String time;
  private LocalDateTime date;

  public SearchObjectCache(){

     date = LocalDateTime.now();
     setDate(date);
  }

  public SearchObjectCache(int id,String url,String category,String text,LocalDateTime date){

  	this.id = id;
  	this.url = url;
  	this.category = category;
  	this.text = text;
  	setDate(date);
  }

   public SearchObjectCache(int id,String url,String category,String text,LocalDateTime date,String location){

	this.id = id;
	this.url = url;
	this.category = category;
	this.text = text;
	setDate(date);
	this.location = location;
  }

  public void setId(int id){
      this.id = id;
  }

  public int getId(){
	  return this.id;
  }

  public void setUrl(String url){
	  this.url = url;
  }

  public String getUrl(){
	  return this.url;
  }

  public void setCategory(String category){
	  this.category = category;
  }

  public String getCategory(){
	  return this.category;
  }

  public void setText(String text){
	  this.text = text;
  }

  public String getText(){
	  return this.text;
  }

  public void setTitle(String title){
	  this.title = title;
  }

  public String getTitle(){
	  return this.title;
  }

  public void setLocation(String location){
	  this.location = location;
  }

  public String getLocation(){
	  return this.location;
  }

  public void setDay(String day){
	  this.day = day;
  }

  public String getDay(){
	  return this.day;
  }

  public void setTime(String time){
	  this.time = time;
  }

  public String getTime(){
	  return this.time;
  }

  public void setDate(LocalDateTime date){

	  if(date == null) date = LocalDateTime.now();
	  this.date = date.plusDays((int)(Math.random()*7));
	  this.day = this.date.getDayOfWeek().toString();
	  this.time = date.toLocalTime().toString().replace(":",".");
  }

  public LocalDateTime getDate(){
	  return this.date;
  }

  @Override
  public String toString(){
	  return text;
  }

	@Override
	public int hashCode(){
		return (id *17) * text.hashCode();
	}

	@Override
	public boolean equals(Object o){

		if(o == null) return false;
		if(o instanceof SearchObjectCache){
			return (TextUtil.isSimilar(((SearchObjectCache)o).getText(),this.getText()))?true:false;
		}
		return true;
	}
}
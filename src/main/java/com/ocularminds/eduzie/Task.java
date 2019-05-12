package com.ocularminds.eduzie;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

public class Task {

	private String id;
	private String title;
	private TaskType type;
	private LocalDate createdOn;
	private boolean done = false;
	private Set tags = new HashSet<>();
	private LocalDate dueOn;

	public Task(){}
	public Task(String title,TaskType type, LocalDate createdOn,String[] tagz,boolean done,LocalDate dueOn){

	   id = Integer.toString((int)(Math.random()*9999));
       this.title = title;
       this.type = type;
       this.createdOn = createdOn;
       this.done = done;
       this.dueOn = dueOn;
       for(int x = 0; x < tagz.length; x++){
		   tags.add(tagz[x]);
	   }
	}

	public String getId(){
		return this.id;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getTitle(){
		return this.title;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public TaskType getType(){
		return this.type;
	}

	public void setType(TaskType t){
		this.type = t;
	}

	public Set<String> getTags(){
		return this.tags;
	}

	public void setTags(Set<String> t){
		this.tags = t;
	}

	public boolean isDone(){
		return this.done;
	}

	public void setDone(boolean done){
		this.done = done;
	}

	public LocalDate getCreatedOn(){
		return this.createdOn;
	}

	public void setCreatedOn(LocalDate createdOn){
		this.createdOn = createdOn;
	}

	public LocalDate getDueOn(){
		return this.dueOn;
	}

	public void setDueOn(LocalDate deuOn){
		this.dueOn = dueOn;
	}

	@Override
	public String toString(){
		return title+" "+type.toString()+" "+createdOn;
	}
}
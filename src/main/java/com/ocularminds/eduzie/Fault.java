package com.ocularminds.eduzie;

public class Fault implements java.io.Serializable{

   private String error;
   private String fault;
   private String group;
   private Object data;

   public Fault(){}

   public Fault(String error,String fault){

      this.error = error;
	  this.fault = fault;

   }

   public Fault(String error,String fault,Object data){

      this.error = error;
      this.fault = fault;
      this.data = data;

   }

   public String getGroup(){
       return group;
   }

   public void setGroup(String group){
	   this.group = group;
   }

   public String getError(){
       return error;
   }

   public void setError(String error){
	   this.error = error;
   }

   public String getFault(){
	   return fault;
   }

   public void setFault(String fault){
	   this.fault = fault;
   }

   public Object getData(){
	   return data;
   }

   public void setData(Object o){
	   this.data = o;
   }

}
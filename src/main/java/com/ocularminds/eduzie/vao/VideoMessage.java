 package com.ocularminds.eduzie.vao;

 public class VideoMessage{

   private String type;
   private String label;
   private String candidate;
   private String text;
   private String id;
   private String to;
   private String sdp;
   private String ice;

   public VideoMessage(){}

   public String getId(){
      return this.id;
   }

   public void setId(String id){
   		this.id = id;
   }

   public String getType(){
      return this.type;
   }

   public void setType(String type){
   		this.type = type;
   }

   public String getSdp(){
      return this.sdp;
   }

   public void setSdp(String sdp){
   		this.sdp = sdp;
   }

   public String getIce(){
      return this.ice;
   }

   public void setIce(String ice){
   		this.ice = ice;
   }

   public String getTo(){
      return this.to;
   }

   public void setTo(String to){
   		this.to = to;
   }

   public String getLabel(){
	   return this.label;
   }

   public void setLabel(String label){
	   this.label = label;
   }

   public String getCandidate(){
	   return this.candidate;
   }

   public void setCandidate(String candidate){
	   this.candidate = candidate;
   }

   public String getText(){
	   return this.text;
   }

   public void setText(String text){
	   this.text = text;
   }

  public String toString(){
	   return new String("").join("","{\"id\":\"",id,"\",\"type\":\"",type,"\",\"label\":\"",label,"\",\"candidate\":\"",candidate,"\",\"text\":\"",text,"\",\"to\":\"",to,"\",\"sdp\":\"",sdp,"\"}");
   }
 }
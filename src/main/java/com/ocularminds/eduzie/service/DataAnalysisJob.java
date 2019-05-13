package com.ocularminds.eduzie.service;

 import org.quartz.Job;
 import org.quartz.JobExecutionContext;
 import org.quartz.JobExecutionException;

// @todo DataAnalysisJob - all quartz jobs by Java 8 concurrency features.
public class DataAnalysisJob implements Job{

	final String attributes = "Robbery,Hijack,Flood,Disaster,Rape,Crush,Suspect,Fraud,"+
					"Illegal,Forge,crime,kill,Attack,Traffic,Slow,Blocked,"+
					"Moving,Accident,Murder,Hold up,Downpour,Fire";

   public void execute(JobExecutionContext context) throws JobExecutionException{



   }
}
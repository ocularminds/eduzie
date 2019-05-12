package com.ocularminds.eduzie.service;

 import org.quartz.Job;
 import org.quartz.JobExecutionContext;
 import org.quartz.JobExecutionException;

 import java.util.List;
 import java.util.Set;
 import java.util.HashSet;

 import com.ocularminds.eduzie.Analyser;
 import com.ocularminds.eduzie.SearchAgent;
 import com.ocularminds.eduzie.SearchBroker;
 import com.ocularminds.eduzie.SearchObjectCache;

//@todo: TrafficSearchJob all quartz jobs by Java 8 concurrency features.
public class TrafficSearchJob implements Job{

	// tweeter@gidi traffik
	Set<String> routes = new HashSet<>();
	final String DATASOURCE = "https://www.facebook.com "+
	"https://twitter.com/Gidi_Traffic "+
	"http://www.tsaboin.com "+
	"http://www.beattraffik.com/ "+
	"http://www.nairaland.com/recent ";//accidents, flood and armed robberies on the site.

	final String attributes = "Hijack,Flood,Accident,Slow,Blocked,Moving,Accident,Hold up,Downpour";

   public void execute(JobExecutionContext context) throws JobExecutionException{

        List<SearchObjectCache> data = null;
        try{

			data = SearchBroker.search(DATASOURCE,attributes,SearchAgent.TRAFFIC_SEARCH_AGENT);
			if(data.size() > 0){
				Analyser.reduce(data);
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
}
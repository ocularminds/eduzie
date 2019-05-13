package com.ocularminds.eduzie.service;

 import org.quartz.Job;
 import org.quartz.JobExecutionContext;
 import org.quartz.JobExecutionException;

 import java.util.List;
 import com.ocularminds.eduzie.SearchAgent;
 import com.ocularminds.eduzie.SearchBroker;
 import com.ocularminds.eduzie.SearchObjectCache;

// @todo SportSearchJob all quartz jobs by Java 8 concurrency features.
public class SportSearchJob implements Job{

	// tweeter@gidi traffik
	final String DATASOURCE = "http://www.completesportsnigeria.com/ "+
	"http://www.newsnow.co.uk/h/World+News/Africa/Nigeria/Sport "+
	"http://www.latestnigeriannews.com/latest-news/sports/ "+
	"http://www.allnigeriasoccer.com/ "+
	"http://www.fourfourtwo.com/nigeria ";

	final String attributes = "Match,game,win,lost,away,home,goalles draw,triumph,defeat";

   public void execute(JobExecutionContext context) throws JobExecutionException{

        List<SearchObjectCache> data = null;
		try{

			SearchBroker.search(DATASOURCE,attributes,SearchAgent.SPORT_SEARCH_AGENT);
			if(data.size() > 0){
				//Analyser.reduce(data);
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
}
package com.ocularminds.eduzie.service;

 import org.quartz.Job;
 import org.quartz.JobExecutionContext;
 import org.quartz.JobExecutionException;

import java.util.List;
import com.ocularminds.eduzie.SearchAgent;
import com.ocularminds.eduzie.SearchBroker;
import com.ocularminds.eduzie.SearchObjectCache;

// @todo EventSearchJob - all quartz jobs by Java 8 concurrency features.
public class EventSearchJob implements Job{

	// tweeter@gidi traffik
	final String DATASOURCE ="http://www.vanguardngr.com/"+new java.text.SimpleDateFormat("yyy/MM/dd").format(new java.util.Date())+"/ "+
	"http://www.thisdaylive.com/news/ "+
	"http://sunnewsonline.com/new/ "+
	"https://www.facebook.com "+
	"http://www.lindaikejisblog.com/ "+
	"http://www.channelstv.com/ "+
	"http://www.channelstv.com/category/local/ "+
	"http://www.vanguardngr.com/ "+
	"http://www.punchng.com "+
	"http://www.nairaland.com/crime "+
	"http://www.nairaland.com/recent "+
	"http://trafficchiefng.com/emergencyAlerts_page.php?query=Enter%20a%20Keyword&type=all&state=all&status=Active";

	final String attributes = "Robbery,Hijack,Flood,Disaster,Rape,Crush,Suspect,Fraud,"+
					"Illegal,Forge,crime,kill,Attack,Blocked,Accident,Murder,Fire";

   public void execute(JobExecutionContext context) throws JobExecutionException{

	   List<SearchObjectCache> data = null;
	   try{

			data = SearchBroker.search(DATASOURCE,attributes,SearchAgent.EVENT_SEARCH_AGENT);
			if(data.size() > 0){
				//Analyser.reduce(data);
			}
	   }catch(Exception e){
		   e.printStackTrace();
	   }

   }
}
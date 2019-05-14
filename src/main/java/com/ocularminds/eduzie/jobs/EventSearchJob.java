package com.ocularminds.eduzie.jobs;

import com.ocularminds.eduzie.Analyser;
import com.ocularminds.eduzie.context.CrimeContext;
import java.util.List;
import com.ocularminds.eduzie.context.Context;
import com.ocularminds.eduzie.WebCrawler;
import com.ocularminds.eduzie.SearchObjectCache;
import java.util.ArrayList;
import java.util.Arrays;

public class EventSearchJob implements Job {

    // tweeter@gidi traffik
    final String DATASOURCE = "http://www.vanguardngr.com/" + new java.text.SimpleDateFormat("yyy/MM/dd").format(new java.util.Date()) + "/ "
            + "http://www.thisdaylive.com/news/ "
            + "http://sunnewsonline.com/new/ "
            + "https://www.facebook.com "
            + "http://www.lindaikejisblog.com/ "
            + "http://www.channelstv.com/ "
            + "http://www.channelstv.com/category/local/ "
            + "http://www.vanguardngr.com/ "
            + "http://www.punchng.com "
            + "http://www.nairaland.com/crime "
            + "http://www.nairaland.com/recent "
            + "http://trafficchiefng.com/emergencyAlerts_page.php?query=Enter%20a%20Keyword&type=all&state=all&status=Active";

    final String attributes = "Robbery,Hijack,Flood,Disaster,Rape,Crush,Suspect,Fraud,"
            + "Illegal,Forge,crime,kill,Attack,Blocked,Accident,Murder,Fire";

    @Override
    public void process() throws JobProcessException {
        java.util.List<String> sources = Arrays.asList(DATASOURCE.split(" "));
        List<SearchObjectCache> data;
        List<Context> activities = new ArrayList<>();
        try {
            sources.forEach(source -> activities.add(new CrimeContext(source, attributes)));
            data = new WebCrawler(activities).crawl();
            if (data.size() > 0) {
                Analyser.reduce(data);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }   
    
}

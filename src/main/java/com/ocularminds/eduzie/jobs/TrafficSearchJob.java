package com.ocularminds.eduzie.jobs;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.ocularminds.eduzie.Analyser;
import com.ocularminds.eduzie.context.Context;
import com.ocularminds.eduzie.WebCrawler;
import com.ocularminds.eduzie.SearchObjectCache;
import com.ocularminds.eduzie.context.TrafficContext;
import java.util.ArrayList;
import java.util.Arrays;

public class TrafficSearchJob implements Job {

    // tweeter@gidi traffik
    Set<String> routes = new HashSet<>();
    final String DATASOURCE = "https://www.facebook.com "
            + "https://twitter.com/Gidi_Traffic "
            + "http://www.tsaboin.com "
            + "http://www.beattraffik.com/ "
            + "http://www.nairaland.com/recent ";//accidents, flood and armed robberies on the site.

    final String attributes = "Hijack,Flood,Accident,Slow,Blocked,Moving,Accident,Hold up,Downpour";
    
    @Override
    public void process() {
        java.util.List<String> sources = Arrays.asList(DATASOURCE.split(" "));
        List<SearchObjectCache> data;
        List<Context> activities = new ArrayList<>();
        try {
            sources.forEach(source -> activities.add(new TrafficContext(source, attributes)));
            data = new WebCrawler(activities).crawl();
            if (data.size() > 0) {
                Analyser.reduce(data);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

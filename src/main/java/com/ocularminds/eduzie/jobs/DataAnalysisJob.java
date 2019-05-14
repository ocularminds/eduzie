package com.ocularminds.eduzie.jobs;

import com.ocularminds.eduzie.Analyser;
import com.ocularminds.eduzie.SearchObjectCache;
import com.ocularminds.eduzie.WebCrawler;
import com.ocularminds.eduzie.context.Context;
import com.ocularminds.eduzie.context.CrimeContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Crawls crime and life threatening related activities
 *
 * @author Festus B. Jejelowo
 */
public class DataAnalysisJob implements Job {

    final static String DATASOURCE = "";
    final String attributes = "Robbery,Hijack,Flood,Disaster,Rape,Crush,Suspect,Fraud,"
            + "Illegal,Forge,crime,kill,Attack,Traffic,Slow,Blocked,"
            + "Moving,Accident,Murder,Hold up,Downpour,Fire";

    /**
     *
     * @throws JobProcessException
     */
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

package com.ocularminds.eduzie.jobs;

import com.ocularminds.eduzie.Analyser;
import java.util.List;
import com.ocularminds.eduzie.context.Context;
import com.ocularminds.eduzie.WebCrawler;
import com.ocularminds.eduzie.SearchObjectCache;
import com.ocularminds.eduzie.context.SportContext;
import java.util.ArrayList;
import java.util.Arrays;

public class SportSearchJob implements Job {

    // tweeter@gidi traffik
    final String DATASOURCE = "http://www.completesportsnigeria.com/ "
            + "http://www.newsnow.co.uk/h/World+News/Africa/Nigeria/Sport "
            + "http://www.latestnigeriannews.com/latest-news/sports/ "
            + "http://www.allnigeriasoccer.com/ "
            + "http://www.fourfourtwo.com/nigeria ";

    final String attributes = "Match,game,win,lost,away,home,goalless draw,triumph,defeat";

    @Override
    public void process() throws JobProcessException {
        java.util.List<String> sources = Arrays.asList(DATASOURCE.split(" "));
        List<SearchObjectCache> data;
        List<Context> activities = new ArrayList<>();
        try {
            sources.forEach(source -> activities.add(new SportContext(source, attributes)));
            data = new WebCrawler(activities).crawl();
            if (data.size() > 0) {
                Analyser.reduce(data);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

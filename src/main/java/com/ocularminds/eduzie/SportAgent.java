package com.ocularminds.eduzie;

/**
 * SearchAgent.java Purpose: Multi-URI Unstructured data search
 *
 * This is Lagos " is a medium for you to share your real life experiences on
 * the Lagos roads as a pedestrian, a commuter that makes use of public
 * transport, a passenger in private vehicle, a vehicle owner or driver - anyone
 * behind the steering wheel of a vehicle.
 *
 * We have heard numerous stories of car-jacking, gun hidden underneath sausage
 * carton robbery, catch-in-the-fly yellow bus robbery, acid threat in hold-up,
 * motorcycle robbery, etc. If you have been a victim of any incidence mentioned
 * above or you have experienced a life threatening incidence in traffic, kindly
 * let us share in your experience as link to saving the lives of many other
 * people and learn from it.
 *
 * My Traffic Links (MTL) is an online medium that seeks to offer you safe and
 * easy drive through the traffic to your various destinations within the Lagos
 * metropolis by providing traffic updates, transport education, re-orientation
 * and trainings that are geared towards the transformation of our driving
 * cultures so that we can have very efficient transportation system whereby
 * every road user shows love and respect to other road users with a heart for
 * obedience of all traffic regulations.
 *
 * Traffic Chief Nigeria is an online crowd-sourced traffic visualization and
 * notification app. The app gathers all the traffic tweets shared by everyday
 * people like you and me on Twitter and does cool stuff with it!
 *
 * @author Festus B Jejelowo
 * @version 1.0 23/11/2015
 */
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import com.jaunt.UserAgent;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.ResponseException;
import com.jaunt.util.HandlerForBinary;

import java.util.concurrent.Callable;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ocularminds.eduzie.common.DateUtil;

public class SportAgent extends SearchAgent implements Callable<List<SearchObjectCache>> {

    private static final Logger logger = Logger.getLogger("Traffic Agent");

    public SportAgent(String source, String attributes) {
        super(source, attributes);
    }

    @Override
    public List<SearchObjectCache> call() {
        logger.log(Level.INFO, "Search Agent - {0} running", source);
        data = new ArrayList<>();//initializing for 10million records
        try {
            search(source, attributes);
            Thread.sleep(10);

        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return data;
    }

    @Override
    public void search(String source, String attributes) {
        try {

            if (!isValidPrefix(source)) {
                return;
            }
            logger.log(Level.INFO, "Searching {0}\n", source);

            HandlerForBinary handlerForBinary = new HandlerForBinary();
            UserAgent userAgent = new UserAgent();

            if (source.contains("https")) {

                System.out.println("setting http proxy for secure site " + source);
                userAgent.settings.checkSSLCerts = false;
            } else {
                userAgent.settings.checkSSLCerts = true;
            }

            userAgent.settings.showHeaders = false;
            userAgent.sendHEAD(source);
            String date = userAgent.response.getHeader("last-modified");
            if (date == null) {
                date = userAgent.response.getHeader("date");
            }
            userAgent.visit(source);

            String datePattern = "EEE, dd MMM yyyy hh:mm:ss z";

            for (String text : Arrays.asList(attributes.split(","))) {
                //find every element who's tagname is div,span,p,li,a,h3,b,i.
                Elements elements;
                if (source.contains("beattraffik")) {
                    elements = userAgent.doc.findEach("<li>");//+text+"|"+text.toLowerCase());
                } else if (source.contains("twitter")) {
                    System.out.println("In Twitter");
                    //elements = userAgent.doc.findEach("<p class=\"TweetTextSize TweetTextSize--26px js-tweet-text tweet-text\">");
                    elements = userAgent.doc.findEach("<p>");
                    System.out.println("Total Element found " + elements.size());
                } else {
                    elements = userAgent.doc.findEvery("<li|div|span|p|abbr|a|h3|b|i|>" + text + "|" + text.toLowerCase());
                }

                for (Element ol : elements) {

                    String d = Long.toString(System.currentTimeMillis());
                    int id = Integer.parseInt(d.substring(d.length() - 6, d.length()));
                    String report = ol.innerText().trim().replaceAll("\\s+", " ").replaceAll(",", " ");
                    if (source.contains("twitter")) {
                        System.out.println(report);
                    }

                    if (report.length() > 7) {

                        if (isSkippable(report)) {
                            continue;
                        } else {

                            if (source.contains("beattraffik") && (!report.toLowerCase().contains(text.toLowerCase()))) {
                                continue;
                            }

                            LocalDateTime dd = (date != null) ? DateUtil.parseWithTime(date, datePattern) : LocalDateTime.now();
                            String ds = dd.toString();

                            //System.out.println(text+","+source+","+report+","+ds);
                            data.add(new SearchObjectCache(id, source, text, report, dd));
                        }
                    }
                }
            }

        } catch (ResponseException | NumberFormatException e) {
//if an HTTP/connection error occurs, handle JauntException.
            logger.log(Level.SEVERE, "Error crawling datasource", e);
        }
    }
}

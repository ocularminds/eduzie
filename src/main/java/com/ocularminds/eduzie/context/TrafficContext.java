package com.ocularminds.eduzie.context;

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
import java.time.LocalDateTime;

import com.jaunt.UserAgent;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.ResponseException;
import com.ocularminds.eduzie.SearchObjectCache;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ocularminds.eduzie.common.DateUtil;

public class TrafficContext extends Context {

    private static Logger logger = Logger.getLogger("Traffic Agent");

    public TrafficContext(String source, String attributes) {
        super(source, attributes);
    }

    @Override
    public List<SearchObjectCache> call() {

        logger.log(Level.INFO, "Search Agent - {0} running", source());
        try {
            search(source, attributes);
            Thread.sleep(10);

        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return results;
    }

    @Override
    public void search(String source, String attributes) {
        try {
            if (!isValidPrefix(source)) {
                return;
            }
            logger.log(Level.INFO, "Searching {0}\n", source);
            UserAgent userAgent = new UserAgent();

            if (source().contains("https")) {
                System.out.println("setting http proxy for secure site " + source());
                userAgent.settings.checkSSLCerts = false;
            } else {
                userAgent.settings.checkSSLCerts = true;
            }

            userAgent.settings.showHeaders = false;
            userAgent.sendHEAD(source());
            String date = userAgent.response.getHeader("last-modified");
            if (date == null) {
                date = userAgent.response.getHeader("date");
            }
            userAgent.visit(source);
            String datePattern = "EEE, dd MMM yyyy hh:mm:ss z";
            for (String text : Arrays.asList(attributes.split(","))) {

                //find every element who's tagname is div,span,p,li,a,h3,b,i.
                Elements elements = null;
                if (source.contains("beattraffik")) {
                    elements = userAgent.doc.findEach("<li>");//+text+"|"+text.toLowerCase());
                } else if (source.contains("tsaboin")) {
                    elements = userAgent.doc.findEach("<li>");//+text+"|"+text.toLowerCase());
                } else if (source.contains("twitter")) {
                    elements = userAgent.doc.findEach("<p>");//+text+"|"+text.toLowerCase());
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
                            } else if (source.contains("tsaboin") && (!report.toLowerCase().contains(text.toLowerCase()))) {
                                continue;
                            } else {
                            }

                            LocalDateTime dd = (date != null) ? DateUtil.parseWithTime(date, datePattern) : LocalDateTime.now();
                            String ds = dd.toString();

                            if (source.contains("tsaboin")) {

                                if (report.toLowerCase().contains("hours ago")) {
                                    report = report.substring(0, report.toLowerCase().indexOf("hours ago") + "hours ago".length()).trim();
                                } else if (report.toLowerCase().contains("minutes ago")) {
                                    report = report.substring(0, report.toLowerCase().indexOf("minutes ago") + "minutes ago".length()).trim();
                                } else if (report.toLowerCase().contains("mins ago")) {
                                    report = report.substring(0, report.toLowerCase().indexOf("mins ago") + "mins ago".length()).trim();
                                } else {
                                }

                                if (report.indexOf("From") > 0) {
                                    report = report.substring(report.indexOf("From"), report.length()).trim();
                                }

                                ds = report.substring(report.lastIndexOf("about") + "about".length() + 1, report.length());
                                if (ds.toLowerCase().contains("hours ago")) {

                                    //05 Jan 2016 5 Hours ago
                                    int hour = Integer.parseInt(ds.substring(0, ds.toLowerCase().indexOf("hours ago")).trim());
                                    ds = DateUtil.hourOrMinutePast(hour, "Hour", "dd MMM yyyy hh:mma");

                                } else if (ds.toLowerCase().contains("minutes ago")) {

                                    //05 Jan 2016 5 Hours ago
                                    int hour = Integer.parseInt(ds.substring(0, ds.toLowerCase().indexOf("minutes ago")).trim());
                                    ds = DateUtil.hourOrMinutePast(hour, "Minutes", "dd MMM yyyy hh:mma");

                                } else {
                                }

                                dd = DateUtil.parseWithTime(ds, "dd MMM yyyy hh:mma");
                                System.out.println("tsaboin " + report + " " + ds);
                            }

                            if (report.contains("Official BeatTraffik Report")) {

                                //22nd Dec 2015 04:22PM DD yyyy hh:mma
                                report = report.replaceAll("  Lagos  Nigeria", "");
                                ds = report.substring(report.lastIndexOf("Official BeatTraffik Report") + "Official BeatTraffik Report".length() + 1, report.length());
                                if (ds.contains("Yesterday")) {
                                    ds = ds.replaceAll("Yesterday", DateUtil.todayOrYesterday("Yesterday"));
                                } else if (ds.contains("Today")) {
                                    ds = ds.replaceAll("Today", DateUtil.todayOrYesterday("Today"));
                                } else {
                                }

                                if (ds.contains("Hours ago")) {

                                    //05 Jan 2016 5 Hours ago
                                    int hour = Integer.parseInt(ds.substring(11, ds.indexOf("Hours ago")).trim());
                                    ds = DateUtil.hourOrMinutePast(hour, "Hour", "dd MMM yyyy hh:mma");

                                } else if (ds.toLowerCase().contains("minutes ago")) {

                                    //05 Jan 2016 5 Hours ago
                                    int hour = Integer.parseInt(ds.substring(11, ds.toLowerCase().indexOf("minutes ago")).trim());
                                    ds = DateUtil.hourOrMinutePast(hour, "Minutes", "dd MMM yyyy hh:mma");

                                }

                                ds = cleanText(ds);
                                dd = DateUtil.parseWithTime(ds, "dd MMM yyyy hh:mma");
                                report = report.substring(0, report.indexOf("Official BeatTraffik Report"));
                                System.out.println("beattfic " + report);
                            }

                            //System.out.println(text+","+source+","+report+","+ds);
                            results.add(new SearchObjectCache(id, source, text, report, dd));
                        }
                    }
                }
            }

        } catch (ResponseException | NumberFormatException e) { //if an HTTP/connection error occurs, handle JauntException.
            logger.log(Level.SEVERE, "Error crawling datasource", e);
        }
    }
}

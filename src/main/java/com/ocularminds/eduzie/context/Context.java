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
 * an online crowd-sourced traffic visualization and notification app. The app
 * gathers all the traffic tweets shared by everyday people like you and me on
 * Twitter and does cool stuff with it!
 *
 * @author Festus B Jejelowo
 * @version 1.0 23/11/2015
 */
import com.ocularminds.eduzie.SearchObjectCache;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class Context implements Callable<List<SearchObjectCache>> {

    String source;
    String attributes;
    ArrayList<SearchObjectCache> results;

    public Context(String source, String attributes) {
        this.source = source;
        this.attributes = attributes;
        results = new ArrayList<>();//initializing for 10million records
    }

    @Override
    public abstract List<SearchObjectCache> call();

    public String tags() {
        return this.attributes;
    }

    public String source(){
        return this.source;
    }

    public abstract void search(String source, String attributes);

    /**
     * checks the validity of the URI prefix
     *
     * @param uri
     * @return
     */
    public boolean isValidPrefix(String uri) {
        return (isFromFile(uri) || isFromRemote(uri) || isFromSecureRemote(uri) || isFromClasspath(uri));
    }

    public boolean isValidSurfix(String surfix) {
        return (surfix.equals(".json") || surfix.equals(".data"));
    }

    public boolean isFromFile(String uri) {
        return (uri.contains("file://"));
    }

    public boolean isFromRemote(String uri) {
        return (uri.contains("http://"));
    }

    public boolean isFromSecureRemote(String uri) {
        return (uri.contains("https://"));
    }

    public boolean isFromClasspath(String uri) {
        return (uri.contains("classpath:attributes/"));
    }

    public boolean isSkippable(String report) {

        return (report.equalsIgnoreCase("Traffic Alert")
                || report.equalsIgnoreCase("trafficalert")
                || report.equalsIgnoreCase("trafficwatch")
                || report.equalsIgnoreCase("Traffic Cam")
                || report.equalsIgnoreCase("trafficlord")
                || report.equalsIgnoreCase("Accident")
                || report.equalsIgnoreCase("Fire Incident")
                || report.contains("Traffic Alert")
                || report.equalsIgnoreCase("Traffic Update"));
    }

    public String cleanText(String ds) {
        String date = ds.replaceAll("nd", "")
                .replaceAll("st", "")
                .replaceAll("th", "")
                .replaceAll("rd", "")
                .replaceAll(" GMT", "")
                .replaceAll("Sun, ", "")
                .replaceAll("Mon, ", "")
                .replaceAll("Tue, ", "")
                .replaceAll("Wed, ", "")
                .replaceAll("Thu, ", "")
                .replaceAll("Fri, ", "")
                .replaceAll("Sat, ", "");
        return date;
    }
}

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

import java.util.concurrent.Callable;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ocularminds.eduzie.common.DateUtil;

public class SportContext extends Context implements Callable<List<SearchObjectCache>> {

    private static final Logger logger = Logger.getLogger(SportContext.class.getName());

    public SportContext(String source, String attributes) {
        super(source, attributes);
    }

    @Override
    public List<SearchObjectCache> call() {
        logger.log(Level.INFO, "Search Agent - {0} running", source);
        try {
            search(source, attributes);
            Thread.sleep(10);

        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return results;
    }

    @Override
    //TODO implements search for SportContext
    public void search(String source, String attributes) {
        
    }
}

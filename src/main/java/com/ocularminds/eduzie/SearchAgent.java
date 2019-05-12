package com.ocularminds.eduzie;
/**
*   SearchAgent.java
*   Purpose: Multi-URI Unstructured data search
*
*   This is Lagos " is a medium for you to share your real life experiences on the Lagos roads as a pedestrian,
*   a commuter that makes use of public transport,
*   a passenger in private vehicle,
*   a vehicle owner or driver - anyone behind the steering wheel of a vehicle.
*
*	We have heard numerous stories of car-jacking, gun hidden underneath sausage carton robbery,
*	catch-in-the-fly yellow bus robbery, acid threat in hold-up, motorcycle robbery, etc.
*	If you have been a victim of any incidence mentioned above or you have experienced a life threatening incidence in traffic,
*	kindly let us share in your experience as link to saving the lives of many other people and learn from it.
*
*	My Traffic Links (MTL) is an online medium that seeks to offer you safe and easy drive
*	through the traffic to your various destinations within the Lagos metropolis by providing traffic updates,
*	transport education, re-orientation and trainings that are geared towards the transformation of our driving
*	cultures so that we can have very efficient transportation system whereby every road user shows love
*	and respect to other road users with a heart for obedience of all traffic regulations.
*
*	an online crowd-sourced traffic visualization and notification app.
*	The app gathers all the traffic tweets shared by everyday people like you and me on Twitter and does cool stuff with it!
*
*   @author Festus B Jejelowo
*   @version 1.0 23/11/2015
*/
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.jaunt.UserAgent;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.util.HandlerForBinary;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ocularminds.eduzie.common.DateUtil;

public abstract class SearchAgent{

	public String source;
	public String attributes;
	public List<SearchObjectCache> data;
	private static Logger logger = Logger.getLogger("SearchAgent");
	public static int EVENT_SEARCH_AGENT = 0;
	public static int TRAFFIC_SEARCH_AGENT = 1;
	public static int SPORT_SEARCH_AGENT = 2;
	public static int OTHER_SEARCH_AGENT = 3;

	public SearchAgent(String source,String attributes){

		this.source = source;
		this.attributes = attributes;
	}

	public abstract List<SearchObjectCache> call();

	/** checks the validity of the URI prefix*/
	public boolean isValidPrefix(String uri){
		return (isFromFile(uri) || isFromRemote(uri) || isFromSecureRemote(uri) || isFromClasspath(uri));
	}

	public boolean isValidSurfix(String surfix){
		return (surfix.equals(".json") || surfix.equals(".data"));
	}

	public boolean isFromFile(String uri){
		return (uri.indexOf("file://") > -1);
	}

	public boolean isFromRemote(String uri){
		return (uri.indexOf("http://") > -1);
	}

	public boolean isFromSecureRemote(String uri){
		return (uri.indexOf("https://") > -1);
	}

	public boolean isFromClasspath(String uri){
		return (uri.indexOf("classpath:attributes/") > -1);
	}

	public boolean isSkippable(String report){

		return (
		  report.equalsIgnoreCase("Traffic Alert") ||
		  report.equalsIgnoreCase("trafficalert") ||
		  report.equalsIgnoreCase("trafficwatch") ||
		  report.equalsIgnoreCase("Traffic Cam")  ||
		  report.equalsIgnoreCase("trafficlord")  ||
		  report.equalsIgnoreCase("Accident")     ||
		  report.equalsIgnoreCase("Fire Incident")||
		  report.contains("Traffic Alert")||
		  report.equalsIgnoreCase("Traffic Update")
	   )?true:false;
	}

	public String cleanText(String ds){

		String date = ds.replaceAll("nd","")
		 .replaceAll("st","")
		 .replaceAll("th","")
		 .replaceAll("rd","")
		 .replaceAll(" GMT","")
		 .replaceAll("Sun, ","")
		 .replaceAll("Mon, ","")
		 .replaceAll("Tue, ","")
		 .replaceAll("Wed, ","")
		 .replaceAll("Thu, ","")
		 .replaceAll("Fri, ","")
		 .replaceAll("Sat, ","");
		 return date;
	}

	public abstract void search(String source,String attributes);
}

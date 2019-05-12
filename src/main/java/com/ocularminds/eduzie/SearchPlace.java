package com.ocularminds.eduzie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.ocularminds.eduzie.vao.Place;

public class SearchPlace {

	public static String EDUZI_GOOGLE_GEOKEY = "AIzaSyDH4-YeYCdjvXN6bbzAoLhypHdu0mCX83o";
	public static String EDUZI_GOOGLE_DIRKEY = "AIzaSyBXjGHEeOP9ZSQCuOivf-9ZlDQZdalHNa4";
	public static String EDUZI_OPWEATHER_KEY = "7343671081dddedf3533db06cbd018ba";
	public static final double EARTH_RADIUS = 6372.8d;
	public static int DRIVE_MODE = 0;
	public static int WALK_MODE = 1;

	//http://api.openweathermap.org/data/2.5/forecast/city?id=524901&APPID=873b6d60d403c65dc6d5a2ba55346d61
	//http://api.openweathermap.org/data/2.5/forecast/daily?q=Lagos,NG&mode=json&units=metric&cnt=7&APPID=7343671081dddedf3533db06cbd018ba

	//web: java $JAVA_OPTS -Dconfig.file=application.conf -Djava.util.logging.config.file=logging.properties -cp "target/lib/*" org.openscoring.server.Main --port $PORT --model-dir "pmml"

   /**
   * calculates the distance between 2 geographic points
   * in this case from users ocation and nearby places
   *
   * @param longitude - double refrenced location logitude
   * @param latitude - double referenced location latitude
   * @param place - Place object being looked for
   */
   public static double distance(double latitude,double longitude,Place place){

	  if(place == null) {

		  System.out.println("calculatet distance place is null");
		  return 0.00d;
	  }

	  double deltalat = Math.toRadians(place.getLatitude() - latitude);
	  double deltalog = Math.toRadians(place.getLongitude() - longitude);

	  double a = Math.pow(Math.sin(deltalat/2),2) +
				 Math.pow(Math.sin(deltalog/2),2) * Math.cos(latitude) * Math.cos(place.getLatitude());
	  double c = 2 * Math.asin(Math.sqrt(a));

	  return EARTH_RADIUS * c;

   }

   public static int nextArrival(double distance,int mode){

	   int t = 0;

	   if(mode == SearchPlace.WALK_MODE){
		   t = (int)((distance*3.6*1000)/(5.0*60));
	   }else{
		   t = (int)(distance/60.0);
	   }

	   return t;

   }

	public static List<Place> parsePlaces(String latitude,String longitude,String json){

		List<Place> places = new ArrayList<Place>();
		try{

			JsonElement e = new JsonParser().parse(json);
			JsonObject  o = e.getAsJsonObject();
			JsonArray data = o.getAsJsonArray("results");

			long id = (long)(Math.random()*1000);

			//5.0 kilometres per hour (km/h) - trecking
			//time - d/5.0

			for(final JsonElement element : data) {

				final JsonObject object = element.getAsJsonObject();
				final JsonObject geo = object.getAsJsonObject("geometry");
				double lat = geo.getAsJsonObject("location").get("lat").getAsDouble();
				double lon = geo.getAsJsonObject("location").get("lng").getAsDouble();
				String name = object.get("name").getAsString();

				Place p = new Place(id,name,lat,lon);
				double distance = distance(Double.parseDouble(latitude),Double.parseDouble(longitude),p);
				p.setDistance(distance);

				places.add(p);
				id++;
			}
		}catch(Exception error){
			error.printStackTrace();
		}

        return places;
	}

	public static String search(String latitude,String longitude,String distance,String type,String name){

		//direcrion api
		//https://maps.googleapis.com/maps/api/directions/json?origin=magodo,%20Lagos&destination=Victorial%20Island,Lagos&key=

		//-33.8670522,151.1957362&radius=500&types=food&name=cruise
		StringBuffer received = new StringBuffer();
		StringBuffer sb = new StringBuffer("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		sb.append("location="+latitude+","+longitude);
		sb.append("&radius="+distance);
		sb.append("&key="+EDUZI_GOOGLE_GEOKEY);

		if(type != null){
			sb.append("&types="+type);
		}

		if(name != null){
			sb.append("&name="+name);
		}

		try{

			URL url = new URL(sb.toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			//con.setRequestMethod("POST");
			//con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			//OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
			//out.write(new Gson().toJson(data));
			//out.close();

			/* 200 represents HTTP OK */
			if(con.getResponseCode() == 200) {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()), 8192);
				for (String line; (line = in.readLine()) != null;) {
					received.append(line);
				}
				in.close();
			} else {
				System.out.println("Failed to fetch places!! "+sb.toString());
			}

		}catch(Exception e){

			System.out.println("[eduzi] error loading data to server.");
			e.printStackTrace();

		}

		System.out.println("Places received from server");
		return received.toString();
	}

	public static String nextWeather(String place){

		StringBuffer received = new StringBuffer();
		StringBuffer sb = new StringBuffer("http://api.openweathermap.org/data/2.5/forecast/daily?");
		sb.append("APPID="+EDUZI_OPWEATHER_KEY);
		sb.append("&mode=json&units=metric&cnt=1");
		sb.append("&q="+place);

		try{

			URL url = new URL(sb.toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);

			/* 200 represents HTTP OK */
			if(con.getResponseCode() == 200) {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()), 8192);
				for (String line; (line = in.readLine()) != null;) {
					received.append(line);
				}
				in.close();
			} else {
				System.out.println("Failed to fetch weaather information!! "+sb.toString());
			}

		}catch(Exception e){

			System.out.println("[eduzi] error loading data to server.");
			e.printStackTrace();

		}

		System.out.println("Places received from server");
		String s = received.toString();
		if(s.length() > 10){

			s = s.substring(s.indexOf("description"),s.lastIndexOf("icon"));//:"sky is clear",
			s = s.substring(14,s.length()-3);//description":"sky is clear","
			if(s.contains("sky is")){
				s = s.replaceAll("sky is","sky would be");
			}else{
				s = "is going to ".concat(s);
			}

		}
		return s;
	}

}
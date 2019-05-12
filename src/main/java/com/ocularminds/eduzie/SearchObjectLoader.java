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
/**
 * A simple SearchObjectLoader loader which reads a file from disk and sends entries to the webservice
 *
 * @author Jejelowo Festus
 */
public class SearchObjectLoader {

    public static void upload(List<SearchObjectCache> data){

		StringBuffer received = new StringBuffer();

		try{

			URL url = new URL("http://localhost:"+System.getenv("PORT")+"/loader/push");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
			out.write(new Gson().toJson(data));
			out.close();

			/* 200 represents HTTP OK */
			if (con.getResponseCode() == 200) {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()), 8192);
				for (String line; (line = in.readLine()) != null;) {
					received.append(line);
				}
				in.close();
			} else {
				System.out.println("Failed to fetch data!!");
			}

		}catch(Exception e){

			System.out.println("[eduzi] error loading data to server.");
			e.printStackTrace();

		}

		System.out.println("Feeds received Feeds downloaded.");
		System.out.println("Server response .... \n");
		System.out.println(received.toString());

     }
  }
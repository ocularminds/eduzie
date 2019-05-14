package com.ocularminds.eduzie;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.ocularminds.eduzie.model.Feed;
import com.ocularminds.eduzie.common.FeedCache;
import com.ocularminds.eduzie.common.DateUtil;
import com.ocularminds.eduzie.common.FileUtil;
import com.ocularminds.eduzie.common.ImageUtil;
import com.ocularminds.eduzie.common.Passwords;
import com.ocularminds.eduzie.common.GeoUtil;
import com.ocularminds.eduzie.model.Place;
import com.ocularminds.eduzie.model.User;
import com.ocularminds.eduzie.model.Comment;
import com.ocularminds.eduzie.model.Post;
import com.ocularminds.eduzie.model.Signal;
import com.ocularminds.eduzie.model.VideoMessage;
import com.ocularminds.eduzie.service.UsersImpl;
import com.ocularminds.eduzie.service.PostsImpl;
import com.ocularminds.eduzie.dao.DbFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.namespace.Namespace;

public class VideoService {

	FeedCache cache;
	Gson gson;
	UsersImpl authorizer;
	PostsImpl writer;
	SocketIOServer server;
	volatile Map<String,SocketIOClient> clients;

	final File upload = new File("upload");
	private static final String USER_SESSION_ID = "USER_SESSION_ID";

	public static void main(String[] args){

		VideoService vs = new VideoService();
		vs.start();
	}

	public VideoService(){

		cache = FeedCache.instance();
		gson = new Gson();
		authorizer = UsersImpl.instance();
		writer = PostsImpl.instance();
		DbFactory.instance();
		clients = new HashMap<String,SocketIOClient>();

	    if (!upload.exists() && !upload.mkdirs()) {
		   throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
	    }

	  Configuration config = new Configuration();
	  config.setHostname("127.0.0.1");
	  config.setPort(7851);
	  config.setMaxFramePayloadLength(1024 * 1024);
	  config.setMaxHttpContentLength(1024 * 1024);

	  server = new SocketIOServer(config);
      server.addNamespace("eduzi-video-chat");

    }

    private Map<String,String> mapValue(String data){

		if(data.contains("{")){
			data = data.substring(1, data.length()-1);           //remove curly brackets
		}
		String[] keyValuePairs = data.split(",");              //split the string to creat key-value pairs
		Map<String,String> map = new HashMap<>();

		for(String pair : keyValuePairs) {

		    String[] entry = pair.split("=");                   //split the pairs to get key and value
		    map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
       }
       return map;
	}

    private synchronized void addClient(SocketIOClient client){
		clients.put(client.getSessionId().toString(),client);
	}

	private void createOrJoin(SocketIOClient client, String room) {

		//List<SocketIOClient>getRoomClients(room);
		int numClients = ((List<SocketIOClient>)((Namespace)client.getNamespace()).getRoomClients(room)).size();
		if (numClients == 0){

			client.joinRoom(room);
			//client.join(room,client.getSessionId());

		} else if (numClients == 1) {

			client.joinRoom(room);
			//server.getRoomOperations(room).sendEvent("joined", room, client.getSessionId().toString());

		} else { // max two clients
			client.sendEvent("full", room);
		}
	}

    public void start(){

     server.addConnectListener(new ConnectListener(){

		 @Override
		 public void onConnect(SocketIOClient client){
			 System.out.println("rcvd client "+client.getSessionId().toString()+" connected.");
		 }
	 });

	  server.addEventListener("authentication", String.class, new DataListener<String>() {
		  @Override
		  public void onData(SocketIOClient client,String data, AckRequest ackRequest) {

			  System.out.println("recv authentication message "+data);
			  Map<String,String> m = mapValue(data);
			  String room = m.get("room");

			  client.sendEvent("authenticated",client.getSessionId().toString());
			  addClient(client);
			  createOrJoin(client,room);
			  System.out.format("sent authenticated,%s\n",client.getSessionId().toString());
			  server.getBroadcastOperations().sendEvent("online",m.get("user"),client.getSessionId().toString());
		  }
	  });

	  server.addEventListener("dial", String.class, new DataListener<String>() {
		  @Override
		  public void onData(SocketIOClient client, String data, AckRequest ackRequest) {

			  System.out.println("recv dial message "+data);
			  Map<String,String> m = mapValue(data);
			  SocketIOClient remote = clients.get(m.get("client"));
			  remote.sendEvent("incomming",client.getSessionId().toString(),m.get("room"));
			  System.out.println("sent incomming from "+client.getSessionId().toString()+" to "+m.get("client"));
		  }
	  });

	  server.addEventListener("accept", String.class, new DataListener<String>() {
		  @Override
		  public void onData(SocketIOClient client, String data, AckRequest ackRequest) {

			  System.out.println("recv accept message "+data);
			  Map<String,String> m = mapValue(data);
			  SocketIOClient remote = clients.get(m.get("client"));
			  createOrJoin(client,m.get("room"));
			  remote.sendEvent("joined",client.getSessionId().toString());
			  System.out.println("sent joined from "+client.getSessionId().toString()+" to "+m.get("client"));
		  }
	  });

	 server.addEventListener("create or join", String.class, new DataListener<String>() {

		@Override
		public void onData(SocketIOClient client, String room, AckRequest ackRequest) {

			//List<SocketIOClient>getRoomClients(room);
			int numClients = ((List<SocketIOClient>)((Namespace)client.getNamespace()).getRoomClients(room)).size();

			if (numClients == 0){

				client.joinRoom(room);//client.join(room,client.getSessionId());
				client.sendEvent("created", room, client.getSessionId().toString());

			} else if (numClients == 1) {

				client.sendEvent("join",room);
				client.joinRoom(room);
				server.getRoomOperations(room).sendEvent("joined", room, client.getSessionId().toString());
				client.sendEvent("ready", room, client.getSessionId().toString());

			} else { // max two clients
				client.sendEvent("full", room);
			}

			String msg = String.format("broadcast(): client %s joined room %s",client.getSessionId().toString(),room);
			server.getRoomOperations(room).sendEvent("message",msg);
		  }

		});
      /*
      todo
       �drop-in� calls, a more sophisticated signaling server, handling multiple clients
       */
	  server.addEventListener("video-message", Signal.class, new DataListener<Signal>() {
		  @Override
		  public void onData(SocketIOClient client, Signal data,AckRequest ackRequest) {
			  System.out.println("recv "+data.getData().getType());
			  if(data == null) return;
			  if(data.getTo() != null){

				  SocketIOClient remote = clients.get(data.getTo());
				  remote.sendEvent("video-message",data);

			  }else{
				  server.getBroadcastOperations().sendEvent("video-message",data);
			  }
              System.out.println("sent to "+data.getTo());
		  }
	  });

	  server.addEventListener("video-data", MediaFile.class, new DataListener<MediaFile>() {
	  		  @Override
	  		  public void onData(SocketIOClient client, MediaFile data, AckRequest ackRequest) {

	  			  String fileName = FileUtil.nextText();
				  client.sendEvent("ffmpeg-output", 0);
				  System.out.println("data.audio.dataURL "+data.audio.dataURL);
				  FileUtil.writeToDisk(data.audio.dataURL, fileName + ".wav");

				  // if it is chrome
				  if (data.video != null) {

					  System.out.println("data.video.dataURL "+data.video.dataURL);
					  FileUtil.writeToDisk(data.video.dataURL, fileName + ".webm");
					  merge(client, fileName);

				  }else{
				      // if it is firefox or if user is recording only audio
				  	 client.sendEvent("merged", fileName + ".wav");
			      }
	  		  }
	  });

	  server.addEventListener("message", String.class, new DataListener<String>() {
		  @Override
		  public void onData(SocketIOClient client, String data, AckRequest ackRequest) {

			  System.out.println("recv message "+data);
			  server.getBroadcastOperations().sendEvent("message",data);
			  System.out.println("sent message "+data);
		  }
	  });

	  server.addEventListener("ipaddr", String.class, new DataListener<String>() {
			  @Override
			  public void onData(SocketIOClient socketIOClient, String data, AckRequest ackRequest) throws Exception {
				  String str = "This is String Hander";
				  server.getBroadcastOperations().sendEvent("ipaddr",server.getConfiguration().getHostname());
			  }
	  });

	  server.addEventListener("binary", byte[].class, new DataListener<byte[]>() {
		  @Override
		  public void onData(SocketIOClient socketIOClient, byte[] buffer, AckRequest ackRequest) throws Exception {
			  System.out.println("binary emit received");
			  String str = "This is Binary Hander";
			  byte[] bt = str.getBytes();
			  server.getBroadcastOperations().sendEvent("string", str);
		  }
	   });

	   server.addEventListener("distance", Place.class, new DataListener<Place>() {
	   	  @Override
		  public void onData(SocketIOClient client, Place place, AckRequest ackRequest) throws Exception {

			  client.sendEvent("distance",GeoUtil.distance(place.getLatitude(),place.getLongitude(),place.getLatitude2(),place.getLongitude2()));
		  }
	  });

	   //distance(double latitude,double longitude,double latitude2,double longitude2)
	   /*
	   Finding and plotting points of interest in the user�s area
	   Annotating content with location informationShowing a user�s position on a map (helping with directions of course!)
	   Turn-by-turn route navigation � using watchPosition
       Up-to-date local information � updates as you move
       */

	   server.addEventListener("location", Place.class, new DataListener<Place>() {
		  @Override
		  public void onData(SocketIOClient client, Place data, AckRequest ackRequest) throws Exception {

	   		System.out.println("recv: location user "+data.getUserId()+" location longitude:"+data.getLongitude()+",latitude:"+data.getLatitude());
	   		String name = null;//"Oshodi Lagos Nigeria Lagos Nigeria Apapa Lagos Nigeria Ikorodu Lagos Nigeria Oworonsoki Ojota Ikeja Agege Ogba Somolu OjuElegba Berger Ojodu ";//"Oshodi";
	   		String type = null;//"routes|point_of_interest";
	   		String distance = "500";
	   		GeoProcessor processor = new GeoProcessor();
	   		List<String> places = processor.process(Double.toString(data.getLatitude()),Double.toString(data.getLongitude()),distance,type,name);
	   		client.sendEvent("location",places);
	   	  }
	  });

       server.start();
	}

  private void sendToUser(final User user, final String event, Object data) {

	final List<UUID> connectionIds = findConnectionIdForUser(user);
	if (connectionIds.isEmpty()) {
		return;
	}

	for (final SocketIOClient client : server.getAllClients()) {
		if (connectionIds.contains(client.getSessionId())) {
			client.sendEvent(event, data);
		}
	}
  }

	private List<UUID> findConnectionIdForUser(User user){
		return new ArrayList<UUID>();
   }

  private void merge(SocketIOClient client, String fileName) {
      //linux
	  //ffmpeg -i video-file.webm -i audio-file.wav -map 0:0 -map 1:0 output-file-name.webm

	  //windows
	  //@echo off
     //"C:\ffmpeg\bin\ffmpeg.exe" -i %1 -i %2  %3
	  /*var FFmpeg = require('fluent-ffmpeg');
	  String audioFile = path.join(__dirname, 'uploads', fileName + '.wav'),
	  videoFile = path.join(__dirname, 'uploads', fileName + '.webm'),
	  String mergedFile = path.join(__dirname, 'uploads', fileName + '-merged.webm');

	  new FFmpeg({source: videoFile})
		  .addInput(audioFile)
		  .on('error', function (err) {
			  client.sendEvent("ffmpeg-error', 'ffmpeg : An error occurred: ' + err.message);
		  })
		  .on('progress', function (progress) {
			  client.sendEvent("ffmpeg-output', Math.round(progress.percent));
		  })
		  .on('end', function () {
			  client.sendEvent("merged', fileName + '-merged.webm');
			  console.log('Merging finished !');

			  // removing audio/video files
			  fs.unlink(audioFile);
			  fs.unlink(videoFile);
		  })
	.saveToFile(mergedFile);*/
}

	  //config.setCloseTimeout(30);
	 /* config.setAuthorizationListener(new AuthorizationListener() {
		  @Override
		  public boolean isAuthorized(HandshakeData data) {
			  String username = data.getSingleUrlParam("username");
			  String password = data.getSingleUrlParam("password");
			  if(true){// username and password correct
				 return true;
			  }else{
				 return false;
			 }
		  }
      });*/



	 //void leaveRoom(String room);
	 //void client.joinRoom(String room);
	 //client.void leaveRoom(String room);
	 //SocketIONamespace	client.getNamespace()
	 //NameSpace.public void join(String room,  UUID sessionId) - client.getSessionId();
     //server.getRoomOperations(String room)
}

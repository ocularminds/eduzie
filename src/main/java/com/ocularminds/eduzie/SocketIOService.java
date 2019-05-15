package com.ocularminds.eduzie;

import java.io.File;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocularminds.eduzie.common.FeedCache;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.ocularminds.eduzie.service.Posts;
import com.ocularminds.eduzie.service.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocketIOService {

    FeedCache cache;
    Gson gson;
    Users authorizer;
    Posts writer;

    final File upload = new File("upload");

    @Autowired
    public SocketIOService(Users users, Posts posts) {
        authorizer = users;
        writer = posts;
        cache = FeedCache.instance();

        if (!upload.exists() && !upload.mkdirs()) {
            throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
        }
        init();
    }

    private void init() {
        Configuration config = new Configuration();
        config.setHostname("127.0.0.1");
        config.setPort(7851);
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);

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
        final SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener((SocketIOClient client) -> {
            String msg = "You are connected!";
            System.out.println("Client " + client + " connected.");
            client.sendEvent("connect", msg);
            System.out.println("Message sent to client " + client.toString());
        });

        server.addEventListener("connect", String.class, (SocketIOClient client, String data, AckRequest req) -> {
            //String k = user + KeyFactory.keyToString(key);
            String msg = "You are connected!";
            System.out.println(msg);
            server.getBroadcastOperations().sendEvent("connect", msg);
        });

        server.addEventListener("message", JsonObject.class, (SocketIOClient client, JsonObject data, AckRequest req) -> {
            //String k = user + KeyFactory.keyToString(key);
            System.out.println("received message " + data);
            server.getBroadcastOperations().sendEvent("message", data);
        });

        server.addEventListener("message", String.class, (SocketIOClient client, String data, AckRequest req) -> {
            //String k = user + KeyFactory.keyToString(key);
            System.out.println("received message " + data);
            server.getBroadcastOperations().sendEvent("message", data);
        });

        server.addEventListener("ipaddr", String.class, (SocketIOClient client, String data, AckRequest req) -> {
            System.out.println("string emit received");
            String str = "This is String Hander";
            server.getBroadcastOperations().sendEvent("string", str);
        });

        server.addEventListener("string", String.class, (SocketIOClient client, String data, AckRequest req) -> {
            System.out.println("string emit received");
            String str = "This is String Hander";
            server.getBroadcastOperations().sendEvent("string", str);
        });

        server.addEventListener("binary", byte[].class, (SocketIOClient client, byte[] buffer, AckRequest req) -> {
            System.out.println("binary emit received");
            String str = "This is Binary Hander";
            byte[] bt = str.getBytes();
            server.getBroadcastOperations().sendEvent("string", str);
        });
        server.start();
    }
}

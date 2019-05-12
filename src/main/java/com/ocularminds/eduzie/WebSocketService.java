package com.ocularminds.eduzie;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import com.google.gson.Gson;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;

@WebSocket
//@todo: WebSocketService - Needs to be fixed. Session is showing compile error
public class WebSocketService {

    private String sender, msg;
    //static List<Fault> messages
    static Map<Session, String> sessions = new ConcurrentHashMap<Session, String>();
	static int nextUserNumber = 1; //Assign to username for next connecting user
	Gson gson = new Gson();

    class Message implements java.io.Serializable{

		String name;
		String message;
		String time;

		public Message(String a,String b,String t){

			name = a;
			message = b;
			time = t;
		}

	}

	public boolean isJson(String m){

	  try {
		  gson.fromJson(m, Object.class);
		  return true;
	  } catch(com.google.gson.JsonSyntaxException ex) {
		  return false;
	  }

	}

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        //do something
        System.out.println("new connection from "+user.toString());
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {

        String username = sessions.get(user);
        sessions.remove(user);
        if(username == null) username = "user";
        broadcast("Server",username + " left the chat");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {

		 if(message.contains("Authenticate^x500")){

			 System.out.println("new authentication - "+message);
			 String username = "User" + nextUserNumber++;
			 sessions.put(user, username);
             broadcast("Server",username + " joined the chat");

		 }else if(message.contains("Chat^web")){

			  System.out.println("new chat message - "+message);
			  broadcast(sessions.get(user),message);

		 }else{

         	 broadcast("server",message);
		 }
    }

	//Sends a message from one user to all users, along with a list of current usernames
	public void broadcast(String sender, String message) {

		sessions.keySet().stream().filter(Session::isOpen).forEach(session -> {
			try {

				if(isJson(message)){
					session.getRemote().sendString(message);
				}else{

					Fault fault = new Fault("00","Success");
					fault.setGroup("chat");
					fault.setData(new Message(sender,message,new SimpleDateFormat("HH:mm:ss").format(new Date())));
					session.getRemote().sendString(gson.toJson(fault));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
  /*
	//Builds a HTML element with a sender-name, a message, and a timestamp,
	private String createHtmlMessageFromSender(String sender, String message) {
		return article().with(
				b(sender + " says:"),
				p(message),
				span().withClass("timestamp").withText(
		).render();
	}

	try {
		session.getRemote().sendString(String.valueOf(new JSONObject()
			.put("userMessage", createHtmlMessageFromSender(sender, message))
			.put("userlist", sessions.values())
		));
	} catch (Exception e) {
		e.printStackTrace();
	}*/
}
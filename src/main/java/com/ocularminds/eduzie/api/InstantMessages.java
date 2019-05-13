/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.api;

import com.google.gson.Gson;
import com.ocularminds.eduzie.Fault;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Festus Jejelowo
 */
@Controller
@MessageMapping("/messaging")
public class InstantMessages {

    Gson gson = new Gson();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    static Map<Session, String> sessions = new ConcurrentHashMap<Session, String>();

    @MessageMapping("/hello")
    @SendTo("/topic/messages")
    public String hello(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return message + ", !";
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {

        String username = sessions.get(user);
        sessions.remove(user);
        if (username == null) {
            username = "user";
        }
        broadcast("Server", username + " left the chat");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {

        if (message.contains("Authenticate^x500")) {
            System.out.println("new authentication - " + message);
            String username = "User" + nextUserNumber++;
            sessions.put(user, username);
            broadcast("Server", username + " joined the chat");

        } else if (message.contains("Chat^web")) {
            System.out.println("new chat message - " + message);
            broadcast(sessions.get(user), message);
        } else {
            broadcast("server", message);
        }
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public Object broadcast(String sender, String message) {
        //@todo session message broad require fix
//        sessions.keySet().forEach(session -> {
//            try {
//                if (isJson(message)) {
//                    return message;
//                } else {
//                    Fault fault = new Fault("00", "Success");
//                    fault.setGroup("chat");
//                    fault.setData(InstantMessage(sender, message, new SimpleDateFormat("HH:mm:ss").format(new Date())));
//                    return fault;
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
        return new Fault();
    }

    public boolean isJson(String m) {
        try {
            gson.fromJson(m, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        //do something
        System.out.println("new connection from " + user.toString());
    }

}

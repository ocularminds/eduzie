/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ocularminds.eduzie.service;

import com.google.gson.Gson;
import com.ocularminds.eduzie.Fault;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 *
 * @author Festus Jejelowo
 */
public class WebSocketHandler extends AbstractWebSocketHandler {

    private String sender, msg;
    //static List<Fault> messages
    static Map<WebSocketSession, String> sessions = new ConcurrentHashMap<WebSocketSession, String>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    Gson gson = new Gson();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("New Text Message Received");
        if (message.toString().contains("token")) {
            System.out.println("new authentication - " + message);
            String username = "User" + nextUserNumber++;
            sessions.put(session, username);
            broadcast("Server", username + " joined the chat");

        } else if (message.toString().contains("Chat^web")) {
            System.out.println("new chat message - " + message.toString());
            broadcast(sessions.get(session), message.toString());
        } else {
            broadcast("server", message.toString());
        }
        session.sendMessage(message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        System.out.println("New Binary Message Received");
        session.sendMessage(message);
    }//Sends a message from one user to all users, along with a list of current usernames

    public void broadcast(String sender, String message) {
        sessions.keySet().stream().filter(WebSocketSession::isOpen).forEach(session -> {
            try {

                if (isJson(message)) {
                    session.sendMessage(new TextMessage(message));
                } else {

                    Fault fault = new Fault("00", "Success");
                    fault.setGroup("chat");
                    fault.setData(new Message(sender, message, new SimpleDateFormat("HH:mm:ss").format(new Date())));
                    session.sendMessage(new TextMessage(gson.toJson(fault)));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    class Message implements java.io.Serializable {

        String name;
        String message;
        String time;

        public Message(String a, String b, String t) {
            name = a;
            message = b;
            time = t;
        }

    }

    public boolean isJson(String m) {
        try {
            gson.fromJson(m, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

}

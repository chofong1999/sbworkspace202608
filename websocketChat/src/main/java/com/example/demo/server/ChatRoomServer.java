package com.example.demo.server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.SocketHandler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
@ServerEndpoint("/ws/chat")
public class ChatRoomServer extends TextWebSocketHandler  {


    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
        System.out.println("Current sessions size: " + sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("User input: " + payload);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
        System.out.println("Current sessions size: " + sessions.size());
    }
//    //用來存放WebSocket已連接的Socket
//    static ArrayList<Session> sessions;
// 
//    @OnMessage
//    public void onMessage(String message, Session session) throws IOException,
//            InterruptedException, EncodeException {
//        System.out.println("User input: " + message);
//        //session.getBasicRemote().sendText("Hello world Mr. " + message);
//        //for (Session s : session.getOpenSessions()) {
//        for (Session s : sessions) {    //對每個連接的Client傳送訊息
//            if (s.isOpen()) {
//                s.getBasicRemote().sendText(message);
//            }
//        }
//    }
// 
//    @OnOpen
//    public void onOpen(Session session) {
//        //紀錄連接到sessions中
//        System.out.println("Client connected");        
//        if (sessions == null) {
//            sessions = new ArrayList<Session>();
//        }
//        sessions.add(session);
//        System.out.println("Current sessions size: " + sessions.size());
//    }
// 
//    @OnClose
//    public void onClose(Session session) {
//        //將連接從sessions中移除
//        System.out.println("Connection closed");
//        if (sessions == null) {
//            sessions = new ArrayList<Session>();
//        }
//        sessions.remove(session);
//        System.out.println("Current sessions size: " + sessions.size());
//    }
}

package com.ocularminds.eduzie;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Basic Echo Client Socket
 * @todo: -WebSocketHandler should be fixed
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class WebSocketHandler {

    private final CountDownLatch closeLatch;

    @SuppressWarnings("unused")
    private Session session;
    private final Object o;

    public WebSocketHandler(Object o) {
        this.o = o;
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {

        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        try {

            Future<Void> fut = session.getRemote().sendStringByFuture(new Gson().toJson(o));
            fut.get(2, TimeUnit.SECONDS);
            session.close(StatusCode.NORMAL, "Data Xchange completed.");

        } catch (InterruptedException | ExecutionException | TimeoutException t) {
            t.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.printf("Got msg: %s%n", msg);
    }
}

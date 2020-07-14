package org.foi.nwtis.ikosmerl.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * Klijent WebSocket-a aplikacije 2.
 * 
 * @author Igor Košmerl
 */
@ClientEndpoint
public class Aplikacija2WebSocketKlijent {

    protected WebSocketContainer container;
    protected Session userSession = null;

    public Aplikacija2WebSocketKlijent() {
        this.container = ContainerProvider.getWebSocketContainer();
    }
    
    public void spojiNaWebSocketServer(String socketServer) {
        try {
            userSession = container.connectToServer(this, new URI(socketServer));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void posaljiPorukuNaWebSocketServer(String poruka) throws IOException {
        userSession.getBasicRemote().sendText(poruka);
    }
    
    public void odspojiKlijenta() throws IOException {
        userSession.close();
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected");
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        System.out.println(msg);
    }
}
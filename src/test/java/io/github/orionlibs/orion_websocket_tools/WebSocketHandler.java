package io.github.orionlibs.orion_websocket_tools;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler
{
    @Override
    public void afterConnectionEstablished(WebSocketSession session)
    {
        System.out.println("new websocketID: " + session.getId());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
    {
        session.sendMessage(new TextMessage(message.getPayload() + " - updated"));
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
    {
        System.out.println("stopping websocketID: " + session.getId());
    }
}

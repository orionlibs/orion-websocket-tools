package io.github.orionlibs.orion_websocket_tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(classes = SpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketConnectionTest
{
    @LocalServerPort
    private int port;


    @Test
    void testWebSocketConnectionWithSockJS() throws Exception
    {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new StringMessageConverter());
        String url = String.format("http://localhost:%d/websocket", port);
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter()
        {
        }).get(2, TimeUnit.SECONDS);
        assertTrue(session.isConnected());
        session.disconnect();
    }
}

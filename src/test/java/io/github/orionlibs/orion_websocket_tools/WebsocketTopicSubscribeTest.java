package io.github.orionlibs.orion_websocket_tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(classes = SpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketTopicSubscribeTest
{
    @LocalServerPort
    private int port;
    private boolean messageReceived;


    @Test
    void testWebSocketConnectionWithSockJS() throws Exception
    {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new StringMessageConverter());
        String url = String.format("http://localhost:%d/websocket", port);
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter()
        {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders)
            {
                session.subscribe("/topic/testtopic", new StompFrameHandler()
                {
                    @Override
                    public Type getPayloadType(StompHeaders headers)
                    {
                        return String.class;
                    }


                    @Override
                    public void handleFrame(StompHeaders headers, Object payload)
                    {
                        System.out.println("Got " + payload);
                        messageReceived = true;
                        //session.disconnect();
                    }
                });
            }
        }).get(2, TimeUnit.SECONDS);
        Utils.applyDelayInSeconds(2);
        String message = "MESSAGE TEST";
        session.send("/app/testtopic", message);
        assertTrue(session.isConnected());
        Utils.applyDelayInSeconds(2);
        session.disconnect();
        assertTrue(messageReceived);
    }
}

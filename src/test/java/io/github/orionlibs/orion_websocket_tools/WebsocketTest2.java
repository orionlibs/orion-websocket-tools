package io.github.orionlibs.orion_websocket_tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

//@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {WebsocketApplication.class}, properties = {
                "spring.main.allow-bean-definition-overriding=true"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketTest2 extends ATest
{
    @LocalServerPort
    private int port;
    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;


    @BeforeEach
    public void setup()
    {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                        Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    }


    @Test
    public void shouldReceiveAMessageFromTheServer() throws Exception
    {
        StompSession session = stompClient
                        .connectAsync("ws://localhost:" + this.port + "/topic/chatmessages", new StompSessionHandlerAdapter()
                        {
                        })
                        .get(1, TimeUnit.SECONDS);
        session.subscribe("/topic/chatmessages", new DefaultStompFrameHandler());
        String message = "MESSAGE TEST";
        session.send("/topic/chatmessages", message.getBytes());
        assertEquals(message, blockingQueue.poll(1, TimeUnit.SECONDS));
    }


    class DefaultStompFrameHandler implements StompFrameHandler
    {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders)
        {
            return byte[].class;
        }


        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o)
        {
            blockingQueue.offer(new String((byte[])o));
        }
    }


    @Configuration
    static class WebsocketSpringContext
    {
    }
}

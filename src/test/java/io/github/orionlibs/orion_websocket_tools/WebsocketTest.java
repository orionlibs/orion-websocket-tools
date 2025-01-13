package io.github.orionlibs.orion_websocket_tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

//@TestInstance(Lifecycle.PER_CLASS)
//@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {WebsocketApplication.class}, properties = {
                "spring.main.allow-bean-definition-overriding=true"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
                "spring.security.enabled=false",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration"
})
public class WebsocketTest extends ATest
{
    WebSocketClient client;
    WebSocketStompClient stompClient;
    @LocalServerPort
    private int port;
    private static final Logger logger = Logger.getLogger(WebsocketTest.class.getName());


    @BeforeEach
    public void setup()
    {
        logger.info("Setting up the tests ...");
        client = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }


    @Test
    void givenWebSocket_whenMessage_thenVerifyMessage() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        StompSessionHandler sessionHandler = new StompSessionHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return null;
            }


            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
            }


            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders)
            {
                logger.info("Connected to the WebSocket ...");
                session.subscribe("/topic/ticks", new StompFrameHandler()
                {
                    @Override
                    public Type getPayloadType(StompHeaders headers)
                    {
                        return Map.class;
                    }


                    @Override
                    public void handleFrame(StompHeaders headers, Object payload)
                    {
                        try
                        {
                            assertThat(payload).isNotNull();
                            assertThat(payload).isInstanceOf(Map.class);
                            @SuppressWarnings("unchecked")
                            Map<String, Integer> map = (Map<String, Integer>)payload;
                            assertThat(map).containsKey("HPE");
                            assertThat(map.get("HPE")).isInstanceOf(Integer.class);
                        }
                        catch(Throwable t)
                        {
                            failure.set(t);
                        }
                        finally
                        {
                            session.disconnect();
                            latch.countDown();
                        }
                    }
                });
            }


            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception)
            {
            }


            @Override
            public void handleTransportError(StompSession session, Throwable exception)
            {
            }
        };
        stompClient.connectAsync("ws://localhost:{port}/stock-ticks/websocket", sessionHandler, this.port);
        if(latch.await(5, TimeUnit.SECONDS))
        {
            if(failure.get() != null)
            {
                fail("Assertion Failed", failure.get());
            }
        }
        else
        {
            fail("Could not receive the message on time");
        }
    }


    @Configuration
    @EnableWebSocketSecurity
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class WebsocketSpringContext
    {
        @Bean
        @Primary
        public MessageMatcherDelegatingAuthorizationManager.Builder messageAuthorizationManager()
        {
            return new MessageMatcherDelegatingAuthorizationManager.Builder();
        }
    }
}

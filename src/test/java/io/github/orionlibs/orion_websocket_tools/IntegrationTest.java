package io.github.orionlibs.orion_websocket_tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(properties = {
                "spring.main.allow-bean-definition-overriding=true"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest
{
    private static Log logger = LogFactory.getLog(IntegrationTest.class);
    //@LocalServerPort
    private int port = 1883;
    private TomcatWebSocketTestServer server;
    private SockJsClient sockJsClient;
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();


    @BeforeEach
    public void setup() throws Exception
    {
        System.setProperty("spring.profiles.active", "test.tomcat");
        server = new TomcatWebSocketTestServer(port);
        server.deployWithInitializer(DispatcherServletInitializer.class, WebSecurityInitializer.class);
        server.start();
        //loginAndSaveJsessionIdCookie("fabrice", "fab123", headers);
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        RestTemplateXhrTransport xhrTransport = new RestTemplateXhrTransport(new RestTemplate());
        transports.add(xhrTransport);
        sockJsClient = new SockJsClient(transports);
    }


    @AfterEach
    public void teardown()
    {
        if(server != null)
        {
            try
            {
                server.undeployConfig();
            }
            catch(Throwable t)
            {
                logger.error("Failed to undeploy application", t);
            }
            try
            {
                server.stop();
            }
            catch(Throwable t)
            {
                logger.error("Failed to stop server", t);
            }
        }
    }


    @Test
    public void getPositions() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        StompSessionHandler handler = new AbstractTestSessionHandler(failure)
        {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders)
            {
                session.subscribe("/app/isAuthenticated", new StompFrameHandler()
                {
                    @Override
                    public Type getPayloadType(StompHeaders headers)
                    {
                        return byte[].class;
                    }


                    @Override
                    public void handleFrame(StompHeaders headers, Object payload)
                    {
                        String json = new String((byte[])payload);
                        logger.debug("Got " + json);
                        assertEquals(json, "Citrix Systems, Inc.");
                        session.disconnect();
                        latch.countDown();
                    }
                });
            }
        };
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.connectAsync("ws://localhost:" + port + "/users", this.headers, handler);
        if(failure.get() != null)
        {
            throw new AssertionError("", failure.get());
        }
        if(!latch.await(5, TimeUnit.SECONDS))
        {
            fail("Portfolio positions not received");
        }
    }


    private static abstract class AbstractTestSessionHandler extends StompSessionHandlerAdapter
    {
        private final AtomicReference<Throwable> failure;


        public AbstractTestSessionHandler(AtomicReference<Throwable> failure)
        {
            this.failure = failure;
        }


        @Override
        public void handleFrame(StompHeaders headers, Object payload)
        {
            logger.error("STOMP ERROR frame: " + headers.toString());
            this.failure.set(new Exception(headers.toString()));
        }


        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex)
        {
            logger.error("Handler exception", ex);
            this.failure.set(ex);
        }


        @Override
        public void handleTransportError(StompSession session, Throwable ex)
        {
            logger.error("Transport failure", ex);
            this.failure.set(ex);
        }
    }
}
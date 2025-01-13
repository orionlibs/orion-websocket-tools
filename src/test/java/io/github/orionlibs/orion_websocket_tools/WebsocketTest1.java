package io.github.orionlibs.orion_websocket_tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.Charset;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest(classes = {WebsocketApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketTest1
{
    @LocalServerPort
    private int port;
    @Autowired
    private AbstractSubscribableChannel clientInboundChannel;
    @Autowired
    private AbstractSubscribableChannel clientOutboundChannel;
    @Autowired
    private AbstractSubscribableChannel brokerChannel;
    private TestChannelInterceptor clientOutboundChannelInterceptor;
    private TestChannelInterceptor brokerChannelInterceptor;


    @BeforeEach
    public void setUp() throws Exception
    {
        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.clientOutboundChannelInterceptor = new TestChannelInterceptor();
        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
        this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
    }


    @Test
    public void getPositions() throws Exception
    {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/app/users");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("fabrice"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        this.clientOutboundChannelInterceptor.setIncludedDestinations("/app/users");
        this.clientInboundChannel.send(message);
        Message<?> reply = this.clientOutboundChannelInterceptor.awaitMessage(5);
        assertNotNull(reply);
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/app/users", replyHeaders.getDestination());
        String json = new String((byte[])reply.getPayload(), Charset.forName("UTF-8"));
        assertEquals(json, "Citrix Systems, Inc.");
    }
    /**
     * Configuration class that un-registers MessageHandler's it finds in the
     * ApplicationContext from the message channels they are subscribed to...
     * except the message handler used to invoke annotated message handling methods.
     * The intent is to reduce additional processing and additional messages not
     * related to the test.
     */
    /*@Configuration
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    static class TestConfig implements ApplicationListener<ContextRefreshedEvent>
    {
        @Autowired
        private List<SubscribableChannel> channels;
        @Autowired
        private List<MessageHandler> handlers;


        @Override
        public void onApplicationEvent(ContextRefreshedEvent event)
        {
            for(MessageHandler handler : handlers)
            {
                if(handler instanceof SimpAnnotationMethodMessageHandler)
                {
                    continue;
                }
                for(SubscribableChannel channel : channels)
                {
                    channel.unsubscribe(handler);
                }
            }
        }
    }*/
}

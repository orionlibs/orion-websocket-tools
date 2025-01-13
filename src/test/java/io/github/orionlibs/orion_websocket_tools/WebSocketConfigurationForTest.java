package io.github.orionlibs.orion_websocket_tools;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@TestConfiguration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketConfigurationForTest implements WebSocketMessageBrokerConfigurer
{
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();
        registry.addEndpoint("/chatwithbots");
        registry.addEndpoint("/chatwithbots").withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.enableSimpleBroker("/topic");
        registry.setCacheLimit(0);
        registry.setPreservePublishOrder(true);
        registry.setApplicationDestinationPrefixes("/app");
    }
}

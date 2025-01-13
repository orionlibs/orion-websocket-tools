package io.github.orionlibs.orion_websocket_tools;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer
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

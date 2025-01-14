package io.github.orionlibs.orion_websocket_tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer
{
    @Autowired
    private AuthChannelInterceptor authChannelInterceptor;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        //registry.addEndpoint("/websocket").setAllowedOrigins("*");
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.enableSimpleBroker("/topic");
        registry.setCacheLimit(0);
        registry.setPreservePublishOrder(true);
        registry.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration)
    {
        registration.interceptors(authChannelInterceptor);
    }
}

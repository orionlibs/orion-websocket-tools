package io.github.orionlibs.orion_websocket_tools;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketClientInboundConfiguration implements WebSocketMessageBrokerConfigurer
{
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration)
    {
        //registration.interceptors(new MyChannelInterceptor());
        registration.interceptors(new ChannelInterceptor()
        {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel)
            {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if(StompCommand.CONNECT.equals(accessor.getCommand()))
                {
                    //access authentication header(s)
                    Authentication user = new UsernamePasswordAuthenticationToken(accessor.getUser(), "password");
                    accessor.setUser(user);
                }
                return message;
            }
        });
    }
}

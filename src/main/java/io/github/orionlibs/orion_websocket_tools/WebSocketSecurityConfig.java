package io.github.orionlibs.orion_websocket_tools;

import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer
{
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages)
    {
        messages.nullDestMatcher().authenticated()
                        .simpSubscribeDestMatchers("/users/queue/errors").permitAll()
                        .simpSubscribeDestMatchers("/topic/*", "/users/**").permitAll()
                        .simpDestMatchers("/app/**").permitAll()
                        //.simpSubscribeDestMatchers("/topic/*", "/user/**").hasRole("USER")
                        //.simpDestMatchers("/app/**").hasRole("USER")
                        .simpTypeMatchers(SUBSCRIBE, MESSAGE).denyAll()
                        .anyMessage().denyAll();
    }


    @Override
    protected boolean sameOriginDisabled()
    {
        // While CSRF is disabled..
        return true;
    }
}

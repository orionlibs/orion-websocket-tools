package io.github.orionlibs.orion_websocket_tools;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;

public class AuthChannelInterceptor implements ChannelInterceptor
{
    private final AuthenticationManager authenticationManager;


    public AuthChannelInterceptor(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel)
    {
        SimpMessageType messageType = StompHeaderAccessor.getCommand(message.getHeaders()).getMessageType();
        if(messageType == SimpMessageType.CONNECT)
        {
            String username = null;
            if(message.getHeaders().containsKey("nativeHeaders"))
            {
                LinkedMultiValueMap nativeHeaders = (LinkedMultiValueMap)message.getHeaders().get("nativeHeaders");
                username = (String)nativeHeaders.getFirst("login");
            }
            String password = StompHeaderAccessor.getPasscode(message.getHeaders());
            Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authenticated = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
        }
        return message;
    }
}

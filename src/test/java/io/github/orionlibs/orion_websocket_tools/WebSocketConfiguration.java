package io.github.orionlibs.orion_websocket_tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements /*WebSocketConfigurer, */WebSocketMessageBrokerConfigurer
{
    @Autowired
    private AuthChannelInterceptor authChannelInterceptor;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        //registry.addEndpoint("/websocket").setAllowedOrigins("*");
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
    }


    @Bean
    public ThreadPoolTaskScheduler heartBeatScheduler()
    {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("websocket-heartbeat-scheduler-");
        scheduler.initialize();
        return scheduler;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.enableSimpleBroker("/topic")
                        .setHeartbeatValue(new long[] {10000, 20000})
                        .setTaskScheduler(heartBeatScheduler());
        registry.setCacheLimit(0);
        registry.setPreservePublishOrder(true);
        registry.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration)
    {
        registration.interceptors(authChannelInterceptor);
    }


    /*@Bean
    public TextWebSocketHandler myTextWebSocketHandler()
    {
        return new WebSocketHandler();
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(myTextWebSocketHandler(), "/websocket")
                        //.addInterceptors(new HttpSessionHandshakeInterceptor())
                        .setAllowedOrigins("*")
                        .withSockJS();
    }*/
}

package io.github.orionlibs.orion_websocket_tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableScheduling
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer
{
    private TaskScheduler messageBrokerTaskScheduler;


    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler)
    {
        this.messageBrokerTaskScheduler = taskScheduler;
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        /*registry.addEndpoint("/users")
                        .withSockJS()
                        .setClientLibraryUrl("http://localhost:8080/myapp/js/sockjsclient.js");*/
        registry.addEndpoint("/users")
                        .setAllowedOrigins("*")
                        .withSockJS()
                        .setStreamBytesLimit(512 * 1024)
                        .setHttpMessageCacheSize(1000)
                        .setDisconnectDelay(30 * 1000);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/app");
        //registry.setPreservePublishOrder(true);
        registry.enableSimpleBroker("/topic", "/queue")
                        .setHeartbeatValue(new long[] {10000, 20000})
                        .setTaskScheduler(this.messageBrokerTaskScheduler);
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(myHandler(), "/myHandler")
                        .setAllowedOrigins("*")
                        .addInterceptors(new HttpSessionHandshakeInterceptor())
                        .withSockJS();
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration)
    {
        registration.setSendTimeLimit(15 * 1000)
                        .setSendBufferSizeLimit(512 * 1024)
                        .setMessageSizeLimit(128 * 1024);
    }


    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer()
    {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }


    @Bean
    public WebSocketHandler myHandler()
    {
        return new WebsocketHandler();
    }
}

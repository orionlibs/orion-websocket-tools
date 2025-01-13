package io.github.orionlibs.orion_websocket_tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@SpringBootApplication
@EnableScheduling
@EnableWebSecurity
public class WebsocketApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(WebsocketApplication.class, args);
    }





    /*@Bean
    @Primary
    public ObjectPostProcessor<Object> objectPostProcessor()
    {
        return new ObjectPostProcessor<Object>()
        {
            @Override
            public <T> T postProcess(T object)
            {
                // Custom processing of objects, if required
                return object;
            }
        };
    }*/
}

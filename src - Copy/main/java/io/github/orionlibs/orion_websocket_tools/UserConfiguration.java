package io.github.orionlibs.orion_websocket_tools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfiguration
{
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder)
    {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("websocketuser")
                        .password(passwordEncoder.encode("password"))
                        .roles("USER")
                        .build());
        manager.createUser(User.withUsername("admin")
                        .password(passwordEncoder.encode("password"))
                        .roles("ADMIN")
                        .build());
        return manager;
    }
}

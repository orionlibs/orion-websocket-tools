package io.github.orionlibs.orion_websocket_tools;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSocketSecurity
public class SecurityConfiguration
{
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer()
    {
        return csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/**");
    }


    @Bean
    public Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer()
    {
        return cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList("http://localhost:8081", "null"));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "websocket", "ws"));
            config.setAllowedHeaders(Arrays.asList("*"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            return config;
        });
    }


    @Bean
    public Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementCustomizer()
    {
        return http -> http.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.cors(corsCustomizer())
                        .csrf(csrfCustomizer())
                        .sessionManagement(sessionManagementCustomizer());
        return http.build();
    }


    /*protected void configureInbound(MessageSecurityMetadataSourceRegistry messages)
    {
        messages.simpDestMatchers("/app/**").authenticated()
                        .simpSubscribeDestMatchers("/topic/**", "/queue/**").authenticated()
                        .anyMessage().authenticated();
    }*/
}

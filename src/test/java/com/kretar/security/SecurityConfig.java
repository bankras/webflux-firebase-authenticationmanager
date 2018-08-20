package com.kretar.security;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String configFile = "/firebase-adminsdk.json";
    private static final String databaseUrl = "https://sample.firebaseio.com";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .securityContextRepository(securityContextRepository())
                .authorizeExchange()
                .anyExchange().authenticated()
                .and().build();
    }

    @Bean
    ServerSecurityContextRepository securityContextRepository() {
        return new FirebaseSecurityContextRepository(authenticationManager());
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager() {
        return new FirebaseAuthenticationManager(configFile, databaseUrl) {
            @Override
            protected List<String> getAuthoritiesForToken(FirebaseToken token) {
                return Collections.singletonList("USER");
            }
        };
    }
}



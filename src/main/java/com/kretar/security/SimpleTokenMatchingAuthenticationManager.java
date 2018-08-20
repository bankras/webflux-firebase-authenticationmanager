package com.kretar.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.Map;

public class SimpleTokenMatchingAuthenticationManager implements ReactiveAuthenticationManager {

    private Map<String, UsernamePasswordAuthenticationToken> users;

    public SimpleTokenMatchingAuthenticationManager(Map<String, UsernamePasswordAuthenticationToken> users) {
        this.users = users;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        if(users.containsKey(authToken)) {
            return Mono.just(users.get(authToken));
        }
        return Mono.empty();
    }
}

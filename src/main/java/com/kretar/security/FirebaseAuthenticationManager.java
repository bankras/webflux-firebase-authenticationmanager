package com.kretar.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FirebaseAuthenticationManager implements ReactiveAuthenticationManager, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAuthenticationManager.class);

    private String firebaseConfigFile;
    private String firebaseDatabaseUrl;

    public FirebaseAuthenticationManager(String firebaseConfigFile, String firebaseDatabaseUrl) {
        this.firebaseConfigFile = firebaseConfigFile;
        this.firebaseDatabaseUrl = firebaseDatabaseUrl;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            if (FirebaseApp.getApps().size() == 0) {
                FileInputStream serviceAccount = new FileInputStream(new ClassPathResource(firebaseConfigFile).getFile());
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl(firebaseDatabaseUrl)
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized");
            }
        } catch (IOException e) {
            throw new BeanInitializationException("Unable to initialize firebase", e);
        }
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(authToken);
            String userId = decodedToken.getUid();
            List<String> authorities = getAuthoritiesForToken(decodedToken);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            auth.setDetails(getDetailsForToken(decodedToken));
            return Mono.just(auth);
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return Mono.empty();
        }
    }

    protected Object getDetailsForToken(FirebaseToken token) {
        return token;
    }

    protected abstract List<String> getAuthoritiesForToken(FirebaseToken token);

}

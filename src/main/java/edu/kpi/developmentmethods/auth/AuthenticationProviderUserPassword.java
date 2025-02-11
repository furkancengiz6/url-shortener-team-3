package edu.kpi.developmentmethods.auth;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
//Простое сопоставление токена и пользователя
private static final Map<String, String> tokenUserMap = new HashMap<>();
    
    static {
        tokenUserMap.put("valid_token_123", "user1@example.com");
        tokenUserMap.put("valid_token_456", "user2@example.com");
    }

  
    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flowable.create(emitter -> {
            String token = (String) authenticationRequest.getSecret();
            // TODO: вам нужно реализовать проверку того, что полученый токен (переменная token)
            // соответствует пользователю в вашей системе и вернуть даные (UserDetails) об этом пользователе
            if (tokenUserMap.containsKey(token)) {
                String userEmail = tokenUserMap.get(token);
                emitter.onNext(new UserDetails(userEmail, new ArrayList<>()));
                emitter.onComplete();
            }  else {
                emitter.onError(new AuthenticationException(new AuthenticationFailed("Invalid token")));
            }
        }, BackpressureStrategy.ERROR);
    }
}

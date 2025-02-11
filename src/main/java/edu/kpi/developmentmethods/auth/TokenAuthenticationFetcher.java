package edu.kpi.developmentmethods.auth;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.micronaut.security.token.config.TokenConfiguration;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class TokenAuthenticationFetcher implements AuthenticationFetcher {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_PREFIX_LOWERCASE = TOKEN_PREFIX.toLowerCase();
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationFetcher.class);
    
    private final Authenticator authenticator;
    private final TokenConfiguration configuration;

    public TokenAuthenticationFetcher(Authenticator authenticator, TokenConfiguration configuration) {
        this.authenticator = authenticator;
        this.configuration = configuration;
    }

    @Override
    public Publisher<Authentication> fetchAuthentication(HttpRequest<?> request) {
        Optional<TokenCredentials> tokenOpt = request.getHeaders().getAuthorization().flatMap(this::parseHeader);

        if (tokenOpt.isPresent()) {
            TokenCredentials tokenCredentials = tokenOpt.get();
            Flowable<AuthenticationResponse> authenticationResponse = Flowable.fromPublisher(authenticator.authenticate(request, tokenCredentials));

            return authenticationResponse.switchMap(response -> {
                if (response.isAuthenticated()) {
                    UserDetails userDetails = response.getUserDetails().get();
                    return Flowable.just(new AuthenticationUserDetailsAdapter(userDetails, configuration.getRolesName(), configuration.getNameKey()));
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Authentication failed");
                    }
                    return Flowable.empty();
                }
            });
        } else {
            return Publishers.empty();
        }
    }

    private Optional<TokenCredentials> parseHeader(String header) {
        if (header == null || !header.toLowerCase().startsWith(TOKEN_PREFIX_LOWERCASE)) {
            return Optional.empty();
        }

        String token = header.substring(TOKEN_PREFIX.length()).trim();
        return token.isBlank() ? Optional.empty() : Optional.of(new TokenCredentials(token));
    }
}

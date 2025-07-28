package pjserrano.authmicroservices.service;

import org.springframework.security.core.userdetails.UserDetails;
import pjserrano.authmicroservices.model.Credentials;
import pjserrano.authmicroservices.model.MyUser;
import reactor.core.publisher.Mono;

public interface IAuthService {

    public Mono<Void> register(MyUser user);
    public Mono<UserDetails> authenticateUser(Credentials credentials);
}

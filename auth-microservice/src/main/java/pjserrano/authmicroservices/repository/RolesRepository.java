package pjserrano.authmicroservices.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pjserrano.authmicroservices.model.Role;
import reactor.core.publisher.Mono;

public interface RolesRepository extends ReactiveCrudRepository<Role, Integer> {
    Mono<Role> findByName(String name);
}
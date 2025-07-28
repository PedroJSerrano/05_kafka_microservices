package pjserrano.authmicroservices.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pjserrano.authmicroservices.model.Credentials;
import pjserrano.authmicroservices.model.MyUser;
import pjserrano.authmicroservices.repository.RolesRepository;
import pjserrano.authmicroservices.repository.UsersRepository;
import pjserrano.authmicroservices.service.IAuthService;
import pjserrano.common.model.RoleType;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository; // Si vas a manejar roles dinámicamente

    @Autowired
    public AuthServiceImpl(UsersRepository usersRepository, RolesRepository rolesRepository) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
    }

    @Override
    public Mono<Void> register(MyUser user) {
        return usersRepository.findByUsername(user.getUsername())
                .flatMap(existingUser ->
                        Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
                                "User with username " + user.getUsername() + " already exists.")))
                .switchIfEmpty(Mono.defer(() -> {
                    user.setNew(true);
                    // TODO: HASHEAR LA CONTRASEÑA AQUÍ (BCrypt)
                    user.setPassword(user.getPassword()); // Temporarily without hashing

                    // Guardar el usuario principal
                    return usersRepository.save(user) // Devuelve Mono<MyUser>
                            .flatMap(savedUser ->
                                    // Buscar el ID del rol 'USER'
                                    rolesRepository.findByName(RoleType.ROLE_USER.getName())
                                            .switchIfEmpty(Mono.error(new IllegalStateException("Default role 'USER' not found in database.")))
                                            .flatMap(userRole ->
                                                    // Asignar el rol al usuario en la tabla de unión
                                                    usersRepository.assignRoleToUser(savedUser.getId(), userRole.getId())
                                                            // Despues de asignar el rol, necesitamos devolver el savedUser
                                                            // para que el tipo de Mono.defer() sea Mono<MyUser>
                                                            .thenReturn(savedUser)
                                            )
                            );
                }))
                .then(); // Este .then() final convierte el Mono<MyUser> del switchIfEmpty a Mono<Void>
    }

    @Override
    public Mono<UserDetails> authenticateUser(Credentials credentials) {
        return usersRepository.findByUsername(credentials.getUsername())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.")))
                .flatMap(user -> {
                    if (credentials.getPassword().equals(user.getPassword())) {
                        return usersRepository.findRolesByUserId(user.getId())
                                .collectList() // <-- Añade .collectList() aquí
                                .defaultIfEmpty(Collections.emptyList())
                                .map(roles -> {
                                    System.out.println("DEBUG: Entrando en la lambda .map con rolesList: " + roles); // Aquí roles será List<String>
                                    user.setRoles(roles);
                                    return user;
                                });
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials."));
                    }
                });
    }
}
package pjserrano.authmicroservices.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pjserrano.authmicroservices.model.MyUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UsersRepository extends ReactiveCrudRepository<MyUser, Integer> {

    // Consulta para encontrar un usuario por nombre de usuario
    // Esta consulta NO carga los roles. La carga de roles se hará por separado.
    Mono<MyUser> findByUsername(String username);

    // método para encontrar los roles de un usuario dado su ID
    @Query("SELECT r.name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    Flux<String> findRolesByUserId(Integer userId);

    // Nuevo método para asignar un rol al usuario en la tabla de unión
    @Modifying // Necesario para operaciones DML (INSERT, UPDATE, DELETE)
    @Query("INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)")
    Mono<Void> assignRoleToUser(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
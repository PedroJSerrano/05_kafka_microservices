package repository;

import model.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

    Flux<Product> findProductsByProductCategory(String productCategory);

    @Transactional
    @Modifying
    Mono<Product> deleteByProductName(String productName);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM product WHERE unitaryProductPrice > ?")
    Mono<Void> deleteProductByPrice(double maxPrice);
}

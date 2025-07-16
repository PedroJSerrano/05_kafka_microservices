package service.impl;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repository.ProductRepository;
import service.IProductService;

import java.time.Duration;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    public ProductRepository productRepository;

    @Override
    public Flux<Product> getProducts() {
        return productRepository.findAll()
                .delayElements(Duration.ofMillis(500));
    }

    @Override
    public Flux<Product> getProductsByCategory(String category) {
        return productRepository.findProductsByProductCategory(category);
    }

    @Override
    public Mono<Product> findByProductCode(int productCode) {
        return productRepository.findById(productCode);

    }

    @Override
    public Mono<Void> newProduct(Product product) {

        return findByProductCode(product.getProductCode())
                .switchIfEmpty(Mono.just(product)
                        .flatMap(p -> {
                            p.setNew(true);
                            return productRepository.save(p);
                        }))
                .then();
    }

    @Override
    public Mono<Product> deleteProduct(int productCode) {
        return findByProductCode(productCode)
                .flatMap(p -> productRepository.deleteById(productCode)
                        .then(Mono.just(p)));
    }

    @Override
    public Mono<Product> updateProduct(int productCode, double price) {
        return findByProductCode(productCode)
                .flatMap(p -> {
                    p.setUnitaryProductPrice(price);
                    return productRepository.save(p);
                });

    }

    @Override
    public Mono<Product> updateAddStock(int productCode, int quantity) {
        return findByProductCode(productCode)
                .flatMap( p -> {
                    p.setQuantityInStock(p.getQuantityInStock() + quantity);
                    return productRepository.save(p);
                });
    }

    @Override
    public Mono<Product> updateSubtractStock(int productCode, int quantity) {
        return findByProductCode(productCode)
                .flatMap( p -> {
                    p.setQuantityInStock(p.getQuantityInStock() - quantity);
                    return productRepository.save(p);
                });
    }
}

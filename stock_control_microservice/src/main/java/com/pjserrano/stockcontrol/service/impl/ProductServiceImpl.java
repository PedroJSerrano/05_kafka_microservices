package com.pjserrano.stockcontrol.service.impl;

import com.pjserrano.stockcontrol.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.pjserrano.stockcontrol.repository.ProductRepository;
import com.pjserrano.stockcontrol.service.IProductService;

import java.time.Duration;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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
                .flatMap(existingProduct ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "Product with code " + product.getProductCode() + " already exists.")))
                .switchIfEmpty(Mono.defer(() -> {
                    product.setNew(true);
                    return productRepository.save(product).doOnSuccess(p -> p.setNew(false));
                }))
                .then(); // Convierte el Mono<Product> resultante de save en Mono<Void>
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

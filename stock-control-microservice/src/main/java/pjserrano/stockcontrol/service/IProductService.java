package pjserrano.stockcontrol.service;

import pjserrano.stockcontrol.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {
    public Flux<Product> getProducts();
    public Flux<Product> getProductsByCategory(String category);
    public Mono<Product> findByProductCode(int productCode);
    public Mono<Void> newProduct(Product product);
    public Mono<Product> deleteProduct(int productCode);
    public Mono<Product> updateProduct(int productCode, double price);
    public Mono<Product> updateAddStock(int productCode, int quantity);
    public Mono<Product> updateSubtractStock(int productCode, int quantity);
}

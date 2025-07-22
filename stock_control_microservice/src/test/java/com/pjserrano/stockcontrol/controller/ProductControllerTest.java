package com.pjserrano.stockcontrol.controller;

import com.pjserrano.stockcontrol.model.Product;
import com.pjserrano.stockcontrol.service.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProducts() {
        Product product1 = new Product(1, "Laptop", "Electronics", 1200.0, 10, false);
        Product product2 = new Product(2, "Mouse", "Electronics", 25.0, 50, false);
        Flux<Product> productsFlux = Flux.just(product1, product2);

        when(productService.getProducts()).thenReturn(productsFlux);

        Flux<Product> result = productController.getProducts();

        StepVerifier.create(result)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        verify(productService, times(1)).getProducts();
    }

    @Test
    void testGetProductsByCategory() {
        String category = "Electronics";
        Product product1 = new Product(1, "Laptop", "Electronics", 1200.0, 10, false);
        Product product2 = new Product(2, "Mouse", "Electronics", 25.0, 50, false);
        Flux<Product> productsFlux = Flux.just(product1, product2);

        when(productService.getProductsByCategory(category)).thenReturn(productsFlux);

        Flux<Product> result = productController.getProductsByCategory(category);

        StepVerifier.create(result)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        verify(productService, times(1)).getProductsByCategory(category);
    }

    @Test
    void testFindByProductCodeFound() {
        int productCode = 1;
        Product product = new Product(productCode, "Laptop", "Electronics", 1200.0, 10, false);

        when(productService.findByProductCode(productCode)).thenReturn(Mono.just(product));

        Mono<Product> resultMono = productController.findByProductCode(productCode);

        StepVerifier.create(resultMono)
                .expectNext(product)
                .verifyComplete();

        verify(productService, times(1)).findByProductCode(productCode);
    }

    @Test
    void testFindByProductCodeNotFound() {
        int productCode = 999;

        when(productService.findByProductCode(productCode)).thenReturn(Mono.empty());

        Mono<Product> resultMono = productController.findByProductCode(productCode);

        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode() == HttpStatus.NOT_FOUND &&
                                "Producto no encontrado".equals(((ResponseStatusException) throwable).getReason()))
                .verify();

        verify(productService, times(1)).findByProductCode(productCode);
    }

    @Test
    void testNewProductSuccess() {
        Product newProduct = new Product(3, "Keyboard", "Peripherals", 75.0, 20, true);

        when(productService.newProduct(any(Product.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> responseMono = productController.newProduct(newProduct);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(productService, times(1)).newProduct(newProduct);
    }

    @Test
    void testDeleteProductSuccess() {
        int productCode = 1;
        Product productToDelete = new Product(productCode, "Laptop", "Electronics", 1200.0, 10, false);

        when(productService.deleteProduct(productCode)).thenReturn(Mono.just(productToDelete));

        Mono<ResponseEntity<Void>> responseMono = productController.deleteProduct(productCode);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NO_CONTENT)
                .verifyComplete();

        verify(productService, times(1)).deleteProduct(productCode);
    }

    @Test
    void testDeleteProductNotFound() {
        int productCode = 999;

        when(productService.deleteProduct(productCode)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> responseMono = productController.deleteProduct(productCode);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(productService, times(1)).deleteProduct(productCode);
    }

    @Test
    void testUpdateProductPriceSuccess() {
        int productCode = 1;
        double newPrice = 1250.0;
        Product updatedProduct = new Product(productCode, "Laptop", "Electronics", newPrice, 10, false);

        when(productService.updateProduct(productCode, newPrice)).thenReturn(Mono.just(updatedProduct));

        Mono<ResponseEntity<Product>> responseMono = productController.updateProduct(productCode, newPrice);

        StepVerifier.create(responseMono)
                .expectNextMatches(response ->
                        response.getStatusCode() == HttpStatus.OK &&
                                response.getBody() != null &&
                                response.getBody().getProductCode() == productCode &&
                                response.getBody().getUnitaryProductPrice() == newPrice) // Usar getUnitaryProductPrice()
                .verifyComplete();

        verify(productService, times(1)).updateProduct(productCode, newPrice);
    }

    @Test
    void testUpdateProductPriceNotFound() {
        int productCode = 999;
        double newPrice = 1250.0;

        when(productService.updateProduct(productCode, newPrice)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Product>> responseMono = productController.updateProduct(productCode, newPrice);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(productService, times(1)).updateProduct(productCode, newPrice);
    }

    @Test
    void testUpdateAddStockSuccess() {
        int productCode = 1;
        int quantity = 5;
        Product updatedProduct = new Product(productCode, "Laptop", "Electronics", 1200.0, 15, false);

        when(productService.updateAddStock(productCode, quantity)).thenReturn(Mono.just(updatedProduct));

        Mono<ResponseEntity<Product>> responseMono = productController.updateAddStock(productCode, quantity);

        StepVerifier.create(responseMono)
                .expectNextMatches(response ->
                        response.getStatusCode() == HttpStatus.OK &&
                                response.getBody() != null &&
                                response.getBody().getQuantityInStock() == 15) // Usar getQuantityInStock()
                .verifyComplete();

        verify(productService, times(1)).updateAddStock(productCode, quantity);
    }

    @Test
    void testUpdateAddStockNotFound() {
        int productCode = 999;
        int quantity = 5;

        when(productService.updateAddStock(productCode, quantity)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Product>> responseMono = productController.updateAddStock(productCode, quantity);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(productService, times(1)).updateAddStock(productCode, quantity);
    }

    @Test
    void testUpdateSubtractStockSuccess() {
        int productCode = 1;
        int quantity = 5;
        Product updatedProduct = new Product(productCode, "Laptop", "Electronics", 1200.0, 5, false);

        when(productService.updateSubtractStock(productCode, quantity)).thenReturn(Mono.just(updatedProduct));

        Mono<ResponseEntity<Product>> responseMono = productController.updateSubtractStock(productCode, quantity);

        StepVerifier.create(responseMono)
                .expectNextMatches(response ->
                        response.getStatusCode() == HttpStatus.OK &&
                                response.getBody() != null &&
                                response.getBody().getQuantityInStock() == 5) // Usar getQuantityInStock()
                .verifyComplete();

        verify(productService, times(1)).updateSubtractStock(productCode, quantity);
    }

    @Test
    void testUpdateSubtractStockNotFound() {
        int productCode = 999;
        int quantity = 5;

        when(productService.updateSubtractStock(productCode, quantity)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Product>> responseMono = productController.updateSubtractStock(productCode, quantity);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(productService, times(1)).updateSubtractStock(productCode, quantity);
    }
}
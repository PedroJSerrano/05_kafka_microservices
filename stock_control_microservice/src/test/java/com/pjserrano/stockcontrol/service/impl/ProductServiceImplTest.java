package com.pjserrano.stockcontrol.service.impl;

import com.pjserrano.stockcontrol.model.Product;
import com.pjserrano.stockcontrol.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.pjserrano.stockcontrol.repository.ProductRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private Product newProduct;
    private Product conflictingProduct;

    @BeforeEach
    void setUp() {
        product1 = new Product(1, "Laptop", "Electronics", 1200.00, 50, false);
        product2 = new Product(2, "Keyboard", "Peripherals", 75.00, 80, false);
        newProduct = new Product(3, "Mouse", "Peripherals", 25.00, 100, true);
        conflictingProduct = new Product(1, "Conflicting Laptop", "Electronics", 1300.00, 60, true);
    }

    // getProducts() tests
    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2));

        StepVerifier.create(productService.getProducts())
                .expectNext(product1, product2)
                .verifyComplete();

        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(productService.getProducts())
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findAll();
    }

    // getProductsByCategory() tests
    @Test
    void shouldReturnProductsByCategory() {
        when(productRepository.findProductsByProductCategory("Electronics")).thenReturn(Flux.just(product1));

        StepVerifier.create(productService.getProductsByCategory("Electronics"))
                .expectNext(product1)
                .verifyComplete();

        verify(productRepository).findProductsByProductCategory("Electronics");
    }

    @Test
    void shouldReturnEmptyFluxWhenCategoryNotFound() {
        when(productRepository.findProductsByProductCategory("Software")).thenReturn(Flux.empty());

        StepVerifier.create(productService.getProductsByCategory("Software"))
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findProductsByProductCategory("Software");
    }

    // findByProductCode() tests
    @Test
    void shouldReturnProductByCode() {
        when(productRepository.findById(1)).thenReturn(Mono.just(product1));

        StepVerifier.create(productService.findByProductCode(1))
                .expectNext(product1)
                .verifyComplete();

        verify(productRepository).findById(1);
    }

    @Test
    void shouldReturnEmptyMonoWhenProductCodeNotFound() {
        when(productRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(productService.findByProductCode(99))
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findById(99);
    }

    // newProduct() tests
    @Test
    void shouldSaveNewProduct() {
        when(productRepository.findById(newProduct.getProductCode())).thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(newProduct));

        StepVerifier.create(productService.newProduct(newProduct))
                .verifyComplete();

        verify(productRepository).findById(newProduct.getProductCode());
        verify(productRepository).save(newProduct);
        assertThat(newProduct.isNew()).isFalse();
    }

    @Test
    void shouldThrowConflictWhenProductExistsOnNewProduct() {
        when(productRepository.findById(conflictingProduct.getProductCode())).thenReturn(Mono.just(product1));

        StepVerifier.create(productService.newProduct(conflictingProduct))
                .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                        ((ResponseStatusException) e).getStatusCode() == HttpStatus.CONFLICT &&
                        e.getMessage().contains("Product with code 1 already exists."))
                .verify();

        verify(productRepository).findById(conflictingProduct.getProductCode());
        verify(productRepository, never()).save(any(Product.class));
        assertThat(conflictingProduct.isNew()).isTrue();
    }

    // deleteProduct() tests
    @Test
    void shouldDeleteAndReturnProduct() {
        when(productRepository.findById(product1.getProductCode())).thenReturn(Mono.just(product1));
        when(productRepository.deleteById(product1.getProductCode())).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(product1.getProductCode()))
                .expectNext(product1)
                .verifyComplete();

        verify(productRepository).findById(product1.getProductCode());
        verify(productRepository).deleteById(product1.getProductCode());
    }

    @Test
    void shouldReturnEmptyMonoWhenProductNotFoundOnDelete() {
        when(productRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(99))
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findById(99);
        verify(productRepository, never()).deleteById(anyInt());
    }

    // updateProduct() tests
    @Test
    void shouldUpdateProductPrice() {
        Product originalProduct = new Product(1, "Laptop", "Electronics", 1200.00, 50, false);
        Product updatedProduct = new Product(1, "Laptop", "Electronics", 1500.00, 50, false);

        when(productRepository.findById(originalProduct.getProductCode())).thenReturn(Mono.just(originalProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productService.updateProduct(originalProduct.getProductCode(), 1500.00))
                .assertNext(p -> {
                    assertThat(p.getUnitaryProductPrice()).isEqualTo(1500.00);
                    assertThat(p.getProductCode()).isEqualTo(originalProduct.getProductCode());
                })
                .verifyComplete();

        verify(productRepository).findById(originalProduct.getProductCode());
        verify(productRepository).save(argThat(p -> p.getUnitaryProductPrice() == 1500.00));
    }

    @Test
    void shouldReturnEmptyMonoWhenProductNotFoundOnPriceUpdate() {
        when(productRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProduct(99, 100.00))
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findById(99);
        verify(productRepository, never()).save(any(Product.class));
    }

    // updateAddStock() tests
    @Test
    void shouldIncreaseProductStock() {
        Product originalProduct = new Product(1, "Laptop", "Electronics", 1200.00, 50, false);
        Product productAfterAddStock = new Product(1, "Laptop", "Electronics", 1200.00, 70, false);

        when(productRepository.findById(originalProduct.getProductCode())).thenReturn(Mono.just(originalProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(productAfterAddStock));

        StepVerifier.create(productService.updateAddStock(originalProduct.getProductCode(), 20))
                .assertNext(p -> assertThat(p.getQuantityInStock()).isEqualTo(70))
                .verifyComplete();

        verify(productRepository).findById(originalProduct.getProductCode());
        verify(productRepository).save(argThat(p -> p.getQuantityInStock() == 70));
    }

    @Test
    void shouldReturnEmptyMonoWhenProductNotFoundOnStockAdd() {
        when(productRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateAddStock(99, 10))
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findById(99);
        verify(productRepository, never()).save(any(Product.class));
    }

    // updateSubtractStock() tests
    @Test
    void shouldDecreaseProductStock() {
        Product originalProduct = new Product(1, "Laptop", "Electronics", 1200.00, 50, false);
        Product productAfterSubtractStock = new Product(1, "Laptop", "Electronics", 1200.00, 30, false);

        when(productRepository.findById(originalProduct.getProductCode())).thenReturn(Mono.just(originalProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(productAfterSubtractStock));

        StepVerifier.create(productService.updateSubtractStock(originalProduct.getProductCode(), 20))
                .assertNext(p -> assertThat(p.getQuantityInStock()).isEqualTo(30))
                .verifyComplete();

        verify(productRepository).findById(originalProduct.getProductCode());
        verify(productRepository).save(argThat(p -> p.getQuantityInStock() == 30));
    }

    @Test
    void shouldReturnEmptyMonoWhenProductNotFoundOnStockSubtract() {
        when(productRepository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateSubtractStock(99, 10))
                .expectNextCount(0)
                .verifyComplete();

        verify(productRepository).findById(99);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldAllowNegativeStockWhenSubtracting() {
        // Current implementation allows negative stock. If not desired, add logic in service.
        Product originalProduct = new Product(1, "Low Stock Item", "Misc", 10.00, 5, false);
        Product productAfterSubtractStock = new Product(1, "Low Stock Item", "Misc", 10.00, -5, false);

        when(productRepository.findById(originalProduct.getProductCode())).thenReturn(Mono.just(originalProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(productAfterSubtractStock));

        StepVerifier.create(productService.updateSubtractStock(originalProduct.getProductCode(), 10))
                .assertNext(p -> assertThat(p.getQuantityInStock()).isEqualTo(-5))
                .verifyComplete();

        verify(productRepository).findById(originalProduct.getProductCode());
        verify(productRepository).save(argThat(p -> p.getQuantityInStock() == -5));
    }
}
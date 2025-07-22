package com.pjserrano.stockcontrol.service.impl;

import com.pjserrano.stockcontrol.StockControlApplication;
import com.pjserrano.stockcontrol.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import com.pjserrano.stockcontrol.repository.ProductRepository;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = StockControlApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password=",
        "spring.r2dbc.initial-sql=classpath:/schema.sql"
})
class ProductServiceImplIntegrationTest {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void newProduct_savesNew() {
        Product newProduct = new Product(999, "New Test Product", "Test Category", 100.0, 10, true);

        StepVerifier.create(productService.newProduct(newProduct))
                .verifyComplete();

        StepVerifier.create(productRepository.findById(newProduct.getProductCode()))
                .assertNext(foundProduct -> {
                    assertThat(foundProduct).isNotNull();
                    assertThat(foundProduct.getProductCode()).isEqualTo(newProduct.getProductCode());
                    assertThat(foundProduct.getProductName()).isEqualTo(newProduct.getProductName());
                    assertThat(foundProduct.isNew()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void newProduct_throwsConflictExistingCode() {
        Product conflictingProduct = new Product(100, "Azucar Modificado", "Alimentacion", 1.2, 20, true);

        StepVerifier.create(productService.newProduct(conflictingProduct))
                .expectErrorMatches(e -> e instanceof ResponseStatusException &&
                        ((ResponseStatusException) e).getStatusCode() == HttpStatus.CONFLICT &&
                        e.getMessage().contains("Product with code 100 already exists."))
                .verify();

        StepVerifier.create(productRepository.findById(100))
                .assertNext(p -> assertThat(p.getProductName()).isEqualTo("Azucar"))
                .verifyComplete();
    }

    @Test
    void getProducts_returnsAll() {
        StepVerifier.create(productService.getProducts())
                .expectNextCount(11)
                .thenConsumeWhile(product -> true, product -> {
                    assertThat(product.getProductCode()).isBetween(100, 110);
                })
                .verifyComplete();
    }

    @Test
    void getProductsByCategory_returnsMatching() {
        String category = "Alimentacion";
        StepVerifier.create(productService.getProductsByCategory(category))
                .expectNextCount(4)
                .thenConsumeWhile(product -> true, product -> {
                    assertThat(product.getProductCategory()).isEqualTo(category);
                })
                .verifyComplete();
    }

    @Test
    void getProductsByCategory_returnsEmptyNoMatch() {
        StepVerifier.create(productService.getProductsByCategory("NonExistentCategory"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByProductCode_returnsProduct() {
        int productCode = 101;
        StepVerifier.create(productService.findByProductCode(productCode))
                .assertNext(product -> {
                    assertThat(product).isNotNull();
                    assertThat(product.getProductCode()).isEqualTo(productCode);
                    assertThat(product.getProductName()).isEqualTo("Leche");
                })
                .verifyComplete();
    }

    @Test
    void findByProductCode_returnsEmptyNotFound() {
        StepVerifier.create(productService.findByProductCode(999))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void deleteProduct_deletesAndReturns() {
        int productCode = 102;
        StepVerifier.create(productService.deleteProduct(productCode))
                .assertNext(deletedProduct -> {
                    assertThat(deletedProduct.getProductCode()).isEqualTo(productCode);
                    assertThat(deletedProduct.getProductName()).isEqualTo("Jabon");
                })
                .verifyComplete();

        StepVerifier.create(productRepository.findById(productCode))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void deleteProduct_returnsEmptyNotFound() {
        StepVerifier.create(productService.deleteProduct(999))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void updateProduct_updatesPrice() {
        int productCode = 103;
        double newPrice = 150.00;

        StepVerifier.create(productService.updateProduct(productCode, newPrice))
                .assertNext(updatedProduct -> {
                    assertThat(updatedProduct.getProductCode()).isEqualTo(productCode);
                    assertThat(updatedProduct.getUnitaryProductPrice()).isEqualTo(newPrice);
                })
                .verifyComplete();

        StepVerifier.create(productRepository.findById(productCode))
                .assertNext(product -> assertThat(product.getUnitaryProductPrice()).isEqualTo(newPrice))
                .verifyComplete();
    }

    @Test
    void updateProduct_returnsEmptyNotFound() {
        StepVerifier.create(productService.updateProduct(999, 500.00))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void updateAddStock_increasesStock() {
        int productCode = 104;
        int quantityToAdd = 5;
        int expectedStock = 20 + 5;

        StepVerifier.create(productService.updateAddStock(productCode, quantityToAdd))
                .assertNext(updatedProduct -> {
                    assertThat(updatedProduct.getProductCode()).isEqualTo(productCode);
                    assertThat(updatedProduct.getQuantityInStock()).isEqualTo(expectedStock);
                })
                .verifyComplete();

        StepVerifier.create(productRepository.findById(productCode))
                .assertNext(product -> assertThat(product.getQuantityInStock()).isEqualTo(expectedStock))
                .verifyComplete();
    }

    @Test
    void updateAddStock_returnsEmptyNotFound() {
        StepVerifier.create(productService.updateAddStock(999, 10))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void updateSubtractStock_decreasesStock() {
        int productCode = 105;
        int quantityToSubtract = 10;
        int expectedStock = 60 - 10;

        StepVerifier.create(productService.updateSubtractStock(productCode, quantityToSubtract))
                .assertNext(updatedProduct -> {
                    assertThat(updatedProduct.getProductCode()).isEqualTo(productCode);
                    assertThat(updatedProduct.getQuantityInStock()).isEqualTo(expectedStock);
                })
                .verifyComplete();

        StepVerifier.create(productRepository.findById(productCode))
                .assertNext(product -> assertThat(product.getQuantityInStock()).isEqualTo(expectedStock))
                .verifyComplete();
    }

    @Test
    void updateSubtractStock_allowsNegative() {
        int productCode = 109; // Agua (Stock inicial: 10)
        int quantityToSubtract = 20;
        int expectedStock = 10 - 20;

        StepVerifier.create(productService.updateSubtractStock(productCode, quantityToSubtract))
                .assertNext(updatedProduct -> {
                    assertThat(updatedProduct.getProductCode()).isEqualTo(productCode);
                    assertThat(updatedProduct.getQuantityInStock()).isEqualTo(expectedStock);
                })
                .verifyComplete();

        StepVerifier.create(productRepository.findById(productCode))
                .assertNext(product -> assertThat(product.getQuantityInStock()).isEqualTo(expectedStock))
                .verifyComplete();
    }

    @Test
    void updateSubtractStock_returnsEmptyNotFound() {
        StepVerifier.create(productService.updateSubtractStock(999, 10))
                .expectNextCount(0)
                .verifyComplete();
    }
}
package com.pjserrano.stockcontrol.controller;

import com.pjserrano.stockcontrol.model.Product;
import com.pjserrano.stockcontrol.service.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

/*@WebFluxTest(
        controllers = ProductController.class,
        excludeAutoConfiguration = { // Excluir las autoconfiguraciones de R2DBC
                R2dbcAutoConfiguration.class,
                R2dbcDataAutoConfiguration.class
        }
)*/
public class ProductControllerTest {
/*
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IProductService productService;

    @Test
    void shouldReturnAllProducts() {
        Product p1 = new Product(100, "Azucar", "Alimentacion", 1.1, 19, false);
        Product p2 = new Product(101, "Leche", "Alimentacion", 1.5, 15, false);
        when(productService.getProducts()).thenReturn(Flux.just(p1, p2));

        webTestClient.get().uri("/products")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(2)
                .contains(p1, p2);
    }

    @Test
    void shouldReturnProductsByCategory() {
        Product p1 = new Product(100, "Azucar", "Alimentacion", 1.1, 19, false);
        when(productService.getProductsByCategory("Alimentacion")).thenReturn(Flux.just(p1));

        webTestClient.get().uri("/products/category?category=Alimentacion")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(1)
                .contains(p1);
    }

    @Test
    void shouldReturnEmptyListWhenCategoryNotFound() {
        when(productService.getProductsByCategory(anyString())).thenReturn(Flux.empty());

        webTestClient.get().uri("/products/category?category=NonExistentCategory")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(0);
    }

    @Test
    void shouldReturnProductByCode() {
        Product product = new Product(100, "Azucar", "Alimentacion", 1.1, 19, false);
        when(productService.findByProductCode(100)).thenReturn(Mono.just(product));

        webTestClient.get().uri("/product?productCode=100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(product);
    }

    @Test
    void shouldReturnNotFoundWhenProductCodeDoesNotExist() {
        when(productService.findByProductCode(anyInt())).thenReturn(Mono.empty());

        webTestClient.get().uri("/product?productCode=999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateNewProduct() {
        Product newProduct = new Product(999, "New Product", "Category", 10.0, 5, true);
        when(productService.newProduct(any(Product.class))).thenReturn(Mono.empty());

        webTestClient.post().uri("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();
    }

    @Test
    void shouldReturnConflictWhenProductAlreadyExists() {
        Product existingProduct = new Product(100, "Existing Product", "Category", 10.0, 5, true);
        when(productService.newProduct(any(Product.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Product with code 100 already exists.")));

        webTestClient.post().uri("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(existingProduct)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        Product deletedProduct = new Product(100, "Azucar", "Alimentacion", 1.1, 19, false);
        when(productService.deleteProduct(100)).thenReturn(Mono.just(deletedProduct));

        webTestClient.delete().uri("/delete?productCode=100")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() {
        when(productService.deleteProduct(anyInt())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/delete?productCode=999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void shouldUpdateProductPrice() {
        Product updatedProduct = new Product(100, "Azucar", "Alimentacion", 15.0, 19, false);
        when(productService.updateProduct(100, 15.0)).thenReturn(Mono.just(updatedProduct));

        webTestClient.put().uri("/update/price?productCode=100&price=15.0")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(updatedProduct);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingPriceForNonExistentProduct() {
        when(productService.updateProduct(anyInt(), anyDouble())).thenReturn(Mono.empty());

        webTestClient.put().uri("/update/price?productCode=999&price=15.0")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldAddStockToProduct() {
        Product updatedProduct = new Product(100, "Azucar", "Alimentacion", 1.1, 29, false);
        when(productService.updateAddStock(100, 10)).thenReturn(Mono.just(updatedProduct));

        webTestClient.put().uri("/update/add-stock?productCode=100&quantity=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(updatedProduct);
    }

    @Test
    void shouldReturnNotFoundWhenAddingStockToNonExistentProduct() {
        when(productService.updateAddStock(anyInt(), anyInt())).thenReturn(Mono.empty());

        webTestClient.put().uri("/update/add-stock?productCode=999&quantity=10")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldSubtractStockFromProduct() {
        Product updatedProduct = new Product(100, "Azucar", "Alimentacion", 1.1, 9, false);
        when(productService.updateSubtractStock(100, 10)).thenReturn(Mono.just(updatedProduct));

        webTestClient.put().uri("/update/subtract-stock?productCode=100&quantity=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(updatedProduct);
    }

    @Test
    void shouldReturnNotFoundWhenSubtractingStockFromNonExistentProduct() {
        when(productService.updateSubtractStock(anyInt(), anyInt())).thenReturn(Mono.empty());

        webTestClient.put().uri("/update/subtract-stock?productCode=999&quantity=10")
                .exchange()
                .expectStatus().isNotFound();
    }*/
}
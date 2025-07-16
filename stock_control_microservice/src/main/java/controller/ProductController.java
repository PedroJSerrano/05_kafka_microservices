package controller;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Importar para 404s
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import service.IProductService;

@RestController
public class ProductController {

    @Autowired
    IProductService productService;

    @GetMapping(value = "products", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping(value = "products/{category}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getProductsByCategory(@PathVariable("category") String category) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping(value ="product")
    public Mono<Product> findByProductCode(@RequestParam("productCode") int productCode) {
        return productService.findByProductCode(productCode)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
    }

    @PostMapping(value = "add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> newProduct(@RequestBody Product product) {
        return productService.newProduct(product)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.CREATED)));
    }

    @DeleteMapping(value = "delete")
    public Mono<ResponseEntity<Void>> deleteProduct(@RequestParam("productCode") int productCode) {
        return productService.deleteProduct(productCode)
                .flatMap(p -> Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .switchIfEmpty(Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND)));
    }

    @PutMapping(value = "update/price")
    public Mono<ResponseEntity<Product>> updateProduct(@RequestParam("productCode") int productCode, @RequestParam("price") double price) {
        return productService.updateProduct(productCode, price)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @PutMapping(value = "update/add-stock")
    public Mono<ResponseEntity<Product>> updateAddStock(@RequestParam("productCode") int productCode, @RequestParam("quantity") int quantity) {
        // Tu patrón original ya era correcto.
        return productService.updateAddStock(productCode, quantity)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @PutMapping(value = "update/subtract-stock")
    public Mono<ResponseEntity<Product>> updateSubtractStock(@RequestParam("productCode") int productCode, @RequestParam("quantity") int quantity) {
        // Tu patrón original ya era correcto.
        return productService.updateSubtractStock(productCode, quantity)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }
}
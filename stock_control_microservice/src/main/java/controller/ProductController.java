package controller;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import service.IProductService;

@RestController
public class ProductController {

    @Autowired
    IProductService productService;

    @GetMapping(value = "products")
    public ResponseEntity<Flux<Product>> getProducts() {
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
    }

    @GetMapping(value = "products/{category}")
    public ResponseEntity<Flux<Product>> getProductsByCategory(@PathVariable("category") String category) {
        return new ResponseEntity<>(productService.getProductsByCategory(category), HttpStatus.OK);
    }

    @GetMapping("product/{productCode}")
    public ResponseEntity<Mono<Product>> findByProductCode(@PathVariable("productCode") int productCode) {
        return new ResponseEntity<>(productService.findByProductCode(productCode), HttpStatus.OK);
    }

    @PostMapping(value = "add", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Void>> newProduct(@RequestBody Product product) {
        return new ResponseEntity<>(productService.newProduct(product), HttpStatus.OK);
    }

    @DeleteMapping(value = "delete")
    public Mono<ResponseEntity<Product>> deleteProduct(@RequestParam("productCode") int productCode) {
        return productService.deleteProduct(productCode)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }

    @PutMapping(value = "update")
    public Mono<ResponseEntity<Product>> updateProduct(@RequestParam("productCode") int productCode, @RequestParam("price") double price) {
        return productService.updateProduct(productCode, price)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }

    @PutMapping(value = "update/add-stock")
    public Mono<ResponseEntity<Product>> updateAddStock(@RequestParam("productCode") int productCode, @RequestParam("quantity") int quantity) {
        return productService.updateAddStock(productCode, quantity)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }

    @PutMapping(value = "update/subtract-stock")
    public Mono<ResponseEntity<Product>> updateSubtractStock(@RequestParam("productCode") int productCode, @RequestParam("quantity") int quantity) {
        return productService.updateSubtractStock(productCode, quantity)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }
}

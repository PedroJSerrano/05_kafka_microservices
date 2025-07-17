package controller;

import model.MyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.IOrderService;
import reactor.core.publisher.Mono; // Importar Mono

@RestController
public class OrderController {

    private final IOrderService orderService;

    // Inyección de dependencias por constructor, es la forma preferida
    @Autowired
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "${api.order.endpoint.path}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> processOrder(@RequestBody MyOrder order) {
        // Llamamos al servicio que ahora devolverá un Mono<Void>
        return orderService.processOrder(order)
                .thenReturn(ResponseEntity.ok().<Void>build())
                .onErrorResume(e -> { // Manejo de errores reactivo
                    System.err.println("Error al procesar el pedido: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
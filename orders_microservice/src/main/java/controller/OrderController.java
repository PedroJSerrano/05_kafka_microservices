package controller;

import model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.IOrderService;

@RestController
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processOrder(@RequestBody Order order) {
        try{
            orderService.processOrder(order);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
}

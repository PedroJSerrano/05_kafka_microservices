package com.pjserrano.orders.controller;

import com.pjserrano.orders.model.MyOrder;
import com.pjserrano.orders.service.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderControllerTest {

    @Mock
    private IOrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessOrderSuccess() {
        MyOrder testOrder = new MyOrder();

        when(orderService.processOrder(any(MyOrder.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> responseMono = orderController.processOrder(testOrder);

        ResponseEntity<Void> responseEntity = responseMono.block();

        verify(orderService, times(1)).processOrder(testOrder);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testProcessOrderFailure() {
        MyOrder testOrder = new MyOrder();
        RuntimeException simulatedException = new RuntimeException("Error simulado de procesamiento");

        when(orderService.processOrder(any(MyOrder.class))).thenReturn(Mono.error(simulatedException));

        Mono<ResponseEntity<Void>> responseMono = orderController.processOrder(testOrder);

        ResponseEntity<Void> responseEntity = responseMono.block();

        verify(orderService, times(1)).processOrder(testOrder);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
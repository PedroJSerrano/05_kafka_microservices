package com.pjserrano.orderprocessor.service.impl;

import com.pjserrano.orderprocessor.client.StockControlClient;
import com.pjserrano.orderprocessor.model.MyOrder;
import com.pjserrano.orderprocessor.service.impl.OrderControlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControlServiceImplTest {

    @Mock
    private StockControlClient stockControlClient;

    @InjectMocks
    private OrderControlServiceImpl orderControlService;

    @BeforeEach
    void setUp() {
        when(stockControlClient.updateSubtractStock(any(MyOrder.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    void whenProcessOrderCalled_thenStockControlClientIsInvoked() {
        MyOrder testOrder = new MyOrder(123, "Producto 123", 5, "Calle");

        orderControlService.processOrder(testOrder);

        verify(stockControlClient).updateSubtractStock(testOrder);
    }
}
package com.pjserrano.orderprocessor.service.impl;

import com.pjserrano.orderprocessor.client.StockControlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.pjserrano.orderprocessor.service.IOrderControlService;
import pjserrano.common.model.MyOrder;

@Service
public class OrderControlServiceImpl implements IOrderControlService {

    private final StockControlClient stockControlClient;

    @Autowired
    public OrderControlServiceImpl(StockControlClient stockControlClient) {
        this.stockControlClient = stockControlClient;
    }

    @Override
    @KafkaListener(topics = "${kafka.topic}", groupId = "group1")
    public void processOrder(MyOrder order) {
        System.out.println("Pedido recibido desde Kafka: " + order);

        // Usamos el cliente para actualizar el stock en el otro microservicio
        // Nota: el método updateSubtractStock debe estar implementado en StockControlClient
        stockControlClient.updateSubtractStock(order)
                .subscribe();
    }
}

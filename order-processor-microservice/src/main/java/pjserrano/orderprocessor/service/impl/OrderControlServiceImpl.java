package pjserrano.orderprocessor.service.impl;

import pjserrano.orderprocessor.client.StockControlClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pjserrano.orderprocessor.service.IOrderControlService;
import pjserrano.common.model.MyOrder;

import java.util.logging.Logger;

@Service
public class OrderControlServiceImpl implements IOrderControlService {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final StockControlClient stockControlClient;

    @Autowired
    public OrderControlServiceImpl(StockControlClient stockControlClient) {
        this.stockControlClient = stockControlClient;
    }

    @Override
    @KafkaListener(topics = "${kafka.topic}", groupId = "group1")
    public void processOrder(MyOrder order) {
        log.info("Pedido recibido desde Kafka: " + order);

        // Usamos el cliente para actualizar el stock en el otro microservicio
        // Nota: el método updateSubtractStock debe estar implementado en StockControlClient
        stockControlClient.updateSubtractStock(order)
                .subscribe();
    }
}

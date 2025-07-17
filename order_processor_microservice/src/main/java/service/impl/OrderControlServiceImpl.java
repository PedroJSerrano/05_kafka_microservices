package service.impl;

import client.StockControlClient;
import model.MyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import service.IOrderControlService;

@Service
public class OrderControlServiceImpl implements IOrderControlService {

    @Autowired
    private StockControlClient stockControlClient;

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

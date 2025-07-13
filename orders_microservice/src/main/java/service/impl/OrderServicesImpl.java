package service.impl;

import model.MyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import service.IOrderService;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderServicesImpl implements IOrderService {

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, MyOrder> kafkaTemplate;

    @Override
    public void processOrder(MyOrder order) {
        CompletableFuture<SendResult<String, MyOrder>> future = kafkaTemplate.send(topic, order);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Error while producing message to kafka", ex);
            }
            System.out.println("Registered order " + result.getProducerRecord().value() +
                    " in topic " + result.getProducerRecord().topic());

        });
    }
}

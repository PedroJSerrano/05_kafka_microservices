package com.pjserrano.orders.service.impl;

import com.pjserrano.orders.model.MyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.pjserrano.orders.service.IOrderService;
import reactor.core.publisher.Mono;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServicesImpl implements IOrderService {

    @Value("${kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, MyOrder> kafkaTemplate;

    @Autowired
    public OrderServicesImpl(KafkaTemplate<String, MyOrder> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> processOrder(MyOrder order) {
        CompletableFuture<SendResult<String, MyOrder>> future = kafkaTemplate.send(topic, order);

        return Mono.fromFuture(future)
                .doOnSuccess(result -> System.out.println("Pedido " + result.getProducerRecord().value() +
                        " registrado en el topic " + result.getProducerRecord().topic()))
                .doOnError(ex -> System.err.println("Error al producir el mensaje a Kafka: " + ex.getMessage()))
                .then(); // `then()` convierte el Mono<SendResult> en Mono<Void>
    }
}
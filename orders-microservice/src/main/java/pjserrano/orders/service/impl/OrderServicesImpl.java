package pjserrano.orders.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pjserrano.orders.service.IOrderService;
import pjserrano.common.model.MyOrder;
import reactor.core.publisher.Mono;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class OrderServicesImpl implements IOrderService {

    private final Logger log = Logger.getLogger(getClass().getName());

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
                .doOnSuccess(result -> log.info("Pedido " + result.getProducerRecord().value() +
                        " registrado en el topic " + result.getProducerRecord().topic()))
                .doOnError(ex -> log.severe("Error al producir el mensaje a Kafka: " + ex.getMessage()))
                .then(); // `then()` convierte el Mono<SendResult> en Mono<Void>
    }
}
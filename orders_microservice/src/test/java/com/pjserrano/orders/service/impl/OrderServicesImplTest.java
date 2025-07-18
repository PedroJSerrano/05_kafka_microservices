package com.pjserrano.orders.service.impl;

import com.pjserrano.orders.OrderApplication;
import com.pjserrano.orders.model.MyOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker; // Nuevo import
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OrderApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
        topics = "topicOrders")
@TestPropertySource(properties = "kafka.topic=topicOrders")
class OrderServicesImplTest {

    @Autowired
    private OrderServicesImpl orderServices;

    @Autowired
    private KafkaTemplate<String, MyOrder> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker; // Nuevo autowired

    private KafkaTestConsumer<MyOrder> consumer;

    private static final String TEST_TOPIC = "topicOrders";

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumer = new KafkaTestConsumer<>(TEST_TOPIC, MyOrder.class);
        consumer.start(consumerProps);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.stop();
        }
    }

    @Test
    void givenOrder_whenProcessOrder_thenMessageIsSentToKafka() {
        MyOrder testOrder = new MyOrder(1, "ProductA", 10, "Address1");

        Mono<Void> result = orderServices.processOrder(testOrder);

        StepVerifier.create(result)
                .verifyComplete();

        MyOrder receivedOrder = consumer.poll(TEST_TOPIC, 10, TimeUnit.SECONDS);

        assertThat(receivedOrder).isNotNull();
        assertThat(receivedOrder.getCodeProductOrdered()).isEqualTo(testOrder.getCodeProductOrdered());
        assertThat(receivedOrder.getName()).isEqualTo(testOrder.getName());
        assertThat(receivedOrder.getQuantity()).isEqualTo(testOrder.getQuantity());
        assertThat(receivedOrder.getAddress()).isEqualTo(testOrder.getAddress());
    }

    static class KafkaTestConsumer<T> {
        private final String topic;
        private final Class<T> valueType;
        private Consumer<String, T> consumer;
        private final AtomicReference<T> lastReceivedMessage = new AtomicReference<>();
        private volatile boolean running = true;

        public KafkaTestConsumer(String topic, Class<T> valueType) {
            this.topic = topic;
            this.valueType = valueType;
        }

        public void start(Map<String, Object> consumerProps) {
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
            consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());

            DefaultKafkaConsumerFactory<String, T> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
            consumer = cf.createConsumer();
            consumer.subscribe(Collections.singleton(topic));

            Executors.newSingleThreadExecutor().execute(() -> {
                while (running && !Thread.currentThread().isInterrupted()) {
                    ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(100));
                    if (!records.isEmpty()) {
                        for (ConsumerRecord<String, T> record : records) {
                            lastReceivedMessage.set(record.value());
                        }
                    }
                }
                if (consumer != null) {
                    consumer.close();
                }
            });
        }

        public T poll(String topic, long timeout, TimeUnit unit) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + unit.toMillis(timeout);
            while (System.currentTimeMillis() < endTime) {
                if (lastReceivedMessage.get() != null) {
                    return lastReceivedMessage.getAndSet(null);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return null;
        }

        public void stop() {
            running = false;
        }
    }
}
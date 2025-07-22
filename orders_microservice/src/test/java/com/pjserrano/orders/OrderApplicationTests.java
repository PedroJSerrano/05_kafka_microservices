package com.pjserrano.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(partitions = 1, topics = "topicOrders")
@ActiveProfiles("test")
class OrderApplicationTests {

    @Test
    void contextLoads() { // Test para probar que spring levanta el contexto
    }
}
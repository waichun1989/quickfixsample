package com.example.demo.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import quickfix.Message;

@Component
public class EventProducer {
    private final Logger log = LoggerFactory.getLogger(EventProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrder(Message order) {
        String payload = order.toString();
        kafkaTemplate.send("fix-orders", payload);
    }

    public void publishExecution(Message exec) {
        String payload = exec.toString();
        kafkaTemplate.send("fix-executions", payload);
    }
}
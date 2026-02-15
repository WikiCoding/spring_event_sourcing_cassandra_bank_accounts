package com.wikicoding.bank_account_lifecycle_engine.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    @Value("${kafka.producer.topic:test1}")
    private String topic;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendMessage(String accountNumber, byte[] message) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, accountNumber, message);
        kafkaTemplate.send(record);
    }
}

package com.wikicoding.bank_account_projections_and_recommendations.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wikicoding.bank_account_projections_and_recommendations.AccountOuterClass;
import com.wikicoding.bank_account_projections_and_recommendations.model.Account;
import com.wikicoding.bank_account_projections_and_recommendations.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final AccountsRepository accountsRepository;

    @KafkaListener(topics = "${kafka.consumer.topic:not_defined_topic}",
            groupId = "${kafka.consumer.group-id:not_defined_group_id}")
    @Transactional
    public void consume(ConsumerRecord<String, byte[]> consumerRecord) {
        byte[] message = consumerRecord.value();
        try {
            AccountOuterClass.Account accountProto = AccountOuterClass.Account.parseFrom(message);
            Account account = new Account(
                    accountProto.getAccountNumber(),
                    accountProto.getAccountName(),
                    accountProto.getBalance(),
                    accountProto.getCreatedAt(),
                    accountProto.getVersion()
            );

            log.info("Received and parsed account state update: {}", account);
            accountsRepository.save(account);

            log.info("Persisted Account Update and Committed Consumer Offset");
        } catch (InvalidProtocolBufferException e) {
            log.error("Error parsing Account, reason: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error. Message: {}", e.getMessage());
        }
    }
}

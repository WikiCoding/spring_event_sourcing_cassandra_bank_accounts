package com.wikicoding.bank_account_lifecycle_engine.cronjob;

import com.wikicoding.bank_account_lifecycle_engine.AccountOuterClass;
import com.wikicoding.bank_account_lifecycle_engine.kafka.KafkaProducer;
import com.wikicoding.bank_account_lifecycle_engine.repository.OutboxDataModel;
import com.wikicoding.bank_account_lifecycle_engine.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountChangesJob {
    private final OutboxRepository outboxRepository;
    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedRateString = "${cron.job.rate.millis:5000}")
    @Transactional
    public void sendUpdates() {
        Iterable<OutboxDataModel> outboxDataModels = outboxRepository.findAll();

        for (OutboxDataModel outboxDataModel : outboxDataModels) {
            AccountOuterClass.Account accountProto = AccountOuterClass.Account.newBuilder()
                    .setAccountNumber(outboxDataModel.getAccountNumber())
                    .setAccountName(outboxDataModel.getAccountName())
                    .setBalance(outboxDataModel.getBalance())
                    .setCreatedAt(outboxDataModel.getCreatedAt())
                    .setVersion(outboxDataModel.getVersion())
                    .build();

            kafkaProducer.sendMessage(outboxDataModel.getAccountNumber(), accountProto.toByteArray());

            outboxRepository.delete(outboxDataModel);
        }
    }
}

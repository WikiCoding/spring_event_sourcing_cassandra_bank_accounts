package com.wikicoding.bank_account_lifecycle_engine.cronjob;

import com.wikicoding.bank_account_lifecycle_engine.AccountOuterClass;
import com.wikicoding.bank_account_lifecycle_engine.kafka.Producer;
import com.wikicoding.bank_account_lifecycle_engine.repository.OutboxDataModel;
import com.wikicoding.bank_account_lifecycle_engine.repository.OutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountChangesJob {
    private final OutboxRepository outboxRepository;
    private final Producer producer;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void sendUpdates() {
        List<OutboxDataModel> outboxDataModels = outboxRepository.findAllByProcessed(false);

        for (OutboxDataModel outboxDataModel : outboxDataModels) {
            var accountProto = AccountOuterClass.Account.newBuilder()
                    .setAccountNumber(outboxDataModel.getAccountNumber())
                    .setAccountName(outboxDataModel.getAccountName())
                    .setBalance(outboxDataModel.getBalance())
                    .setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .setVersion(outboxDataModel.getVersion())
                    .build();

            producer.sendMessage(outboxDataModel.getAccountNumber(), accountProto.toByteArray());

            outboxRepository.delete(outboxDataModel);
        }
    }
}

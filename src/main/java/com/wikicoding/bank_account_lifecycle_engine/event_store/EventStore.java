package com.wikicoding.bank_account_lifecycle_engine.event_store;


import com.wikicoding.bank_account_lifecycle_engine.domain.Account;
import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.repository.EventDataModel;
import com.wikicoding.bank_account_lifecycle_engine.repository.EventsRepository;
import com.wikicoding.bank_account_lifecycle_engine.repository.SnapshotDataModel;
import com.wikicoding.bank_account_lifecycle_engine.repository.SnapshotsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStore {
    @Value("${event.store.snapshot.interval}")
    private int SNAPSHOT_INTERVAL;
    private final EventsRepository eventsRepository;
    private final SnapshotsRepository snapshotsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void persistState(List<DomainEvent> events, Account account) {
        for (DomainEvent event : events) {
            saveEvent(event);
            if (event.getVersion() % SNAPSHOT_INTERVAL == 0) saveSnapshot(account);
        }
    }

    private void saveEvent(DomainEvent event) {
        String eventJson = objectMapper.writeValueAsString(event);

        EventDataModel eventDataModel = new EventDataModel(
                event.getAccountNumber(),
                event.getVersion(),
                event.getEventType(),
                eventJson
        );

        log.info("Saving event: {}", eventJson);

        eventsRepository.save(eventDataModel);
    }

    private void saveSnapshot(Account account) {
        SnapshotDataModel snapshot = new SnapshotDataModel(
                account.getAccountNumber(),
                account.getAccountName(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getVersion()
        );

        log.info("Saving snapshot: {}", snapshot);

        snapshotsRepository.save(snapshot);
    }

    public List<DomainEvent> getAccountEvents(String accountNumber) {
        List<EventDataModel> eventDataModels = eventsRepository.findAllByAccountNumber(accountNumber);

        List<DomainEvent> domainEvents = convertEventDataModelsToDomainEvents(eventDataModels);

        return domainEvents.stream().sorted(Comparator.comparing(DomainEvent::getVersion)).toList();
    }

    private @NonNull List<DomainEvent> convertEventDataModelsToDomainEvents(List<EventDataModel> eventDataModels) {
        List<DomainEvent> domainEvents = new ArrayList<>();

        for (EventDataModel eventDataModel : eventDataModels) {
            if (eventDataModel.getEventType().equals(CreatedAccountEvent.class.getSimpleName())) {
                CreatedAccountEvent event = objectMapper.readValue(eventDataModel.getEventJson(), CreatedAccountEvent.class);
                domainEvents.add(event);
                continue;
            }

            if (eventDataModel.getEventType().equals(DepositedMoneyEvent.class.getSimpleName())) {
                DepositedMoneyEvent event = objectMapper.readValue(eventDataModel.getEventJson(), DepositedMoneyEvent.class);
                domainEvents.add(event);
                continue;
            }

            if (eventDataModel.getEventType().equals(WithdrewMoneyEvent.class.getSimpleName())) {
                WithdrewMoneyEvent event = objectMapper.readValue(eventDataModel.getEventJson(), WithdrewMoneyEvent.class);
                domainEvents.add(event);
            }
        }

        return domainEvents;
    }

    public Optional<Account> getAccountSnapshotState(String accountNumber) {
        Optional<SnapshotDataModel> snapshot = snapshotsRepository.findById(accountNumber);

        if (snapshot.isEmpty()) return Optional.empty();

        SnapshotDataModel snapshotDataModel = snapshot.get();

        Account account = setAccountCurrentState(accountNumber, snapshotDataModel);

        return Optional.of(account);
    }

    private @NonNull Account setAccountCurrentState(String accountNumber, SnapshotDataModel snapshotDataModel) {
        Account account = new Account();
        account.setStateFromSnapshot(snapshotDataModel);

        log.info("Finding remaining events of accountNumber: {}, from latest snapshot version: {}",
                account.getAccountNumber(),
                snapshotDataModel.getVersion());

        List<EventDataModel> eventDataModels = eventsRepository
                .findAllByAccountNumberAndVersionGreaterThan(accountNumber, account.getVersion());

        List<DomainEvent> domainEvents = convertEventDataModelsToDomainEvents(eventDataModels);

        for (DomainEvent event : domainEvents) account.apply(event);

        log.info("Account rebuilt from snapshot: {} With {} remaining events applied since last snapshot",
                account,
                domainEvents.size());

        return account;
    }
}

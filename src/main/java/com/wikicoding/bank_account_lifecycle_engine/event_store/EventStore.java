package com.wikicoding.bank_account_lifecycle_engine.event_store;


import com.wikicoding.bank_account_lifecycle_engine.events.CreatedAccountEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DepositedMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.DomainEvent;
import com.wikicoding.bank_account_lifecycle_engine.events.WithdrewMoneyEvent;
import com.wikicoding.bank_account_lifecycle_engine.repository.EventDataModel;
import com.wikicoding.bank_account_lifecycle_engine.repository.EventsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventStore {
    private final EventsRepository eventsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void persistState(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            if (event instanceof CreatedAccountEvent createdAccountEvent) {
                handleCreatedAccountEvent(createdAccountEvent);
                continue;
            }

            if (event instanceof DepositedMoneyEvent depositedMoneyEvent) {
                handleDepositedMoneyEvent(depositedMoneyEvent);
                continue;
            }

            if (event instanceof WithdrewMoneyEvent withdrewMoneyEvent) {
                handleWithdrewMoneyEvent(withdrewMoneyEvent);
            }
        }
    }

    private void handleCreatedAccountEvent(CreatedAccountEvent createdAccountEvent) {
        String eventJson = objectMapper.writeValueAsString(createdAccountEvent);

        EventDataModel eventDataModel = new EventDataModel(
                createdAccountEvent.getAccountNumber(),
                UUID.randomUUID().toString(),
                createdAccountEvent.getEventType(),
                1,
                eventJson
        );

        eventsRepository.save(eventDataModel);
    }

    private void handleDepositedMoneyEvent(DepositedMoneyEvent event) {
        String eventJson = objectMapper.writeValueAsString(event);

        EventDataModel eventDataModel = new EventDataModel(
                event.getAccountNumber(),
                UUID.randomUUID().toString(),
                event.getEventType(),
                event.getVersion(),
                eventJson
        );

        eventsRepository.save(eventDataModel);
    }

    private void handleWithdrewMoneyEvent(WithdrewMoneyEvent event) {
        String eventJson = objectMapper.writeValueAsString(event);

        EventDataModel eventDataModel = new EventDataModel(
                event.getAccountNumber(),
                UUID.randomUUID().toString(),
                event.getEventType(),
                event.getVersion(),
                eventJson
        );

        eventsRepository.save(eventDataModel);
    }

    public List<DomainEvent> getAccountEvents(String accountNumber) {
        List<EventDataModel> eventDataModels = eventsRepository.findAllByAccountNumber(accountNumber);

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
        return domainEvents.stream().sorted(Comparator.comparing(DomainEvent::getVersion)).toList();
    }
}

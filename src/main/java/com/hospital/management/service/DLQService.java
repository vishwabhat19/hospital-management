package com.hospital.management.service;

import com.hospital.management.entity.DLQMessage;
import com.hospital.management.repository.DLQRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service class for handling Dead Letter Queue (DLQ) messages.
 * This service is responsible for persisting failed messages
 * into the DLQ repository for future analysis and debugging.
 */
@Service
public class DLQService {

    private final DLQRepository dlqRepository;

    public DLQService(DLQRepository dlqRepository) {
        this.dlqRepository = dlqRepository;
    }

    /**
     * Saves a failed message into the Dead Letter Queue repository.
     * The message is converted to a string and stored along with
     * the current timestamp.
     *
     * @param message the failed message to be stored
     */
    public void saveFailedMessage(Map<String, Object> message) {
        DLQMessage dlqMessage = new DLQMessage();
        dlqMessage.setMessageContent(message.toString());
        dlqMessage.setTimestamp(LocalDateTime.now());
        dlqRepository.save(dlqMessage);
    }
}

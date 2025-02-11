package com.hospital.management.consumer;

import com.hospital.management.service.DLQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Listener component for handling messages in the Dead Letter Queue (DLQ).
 * This class listens for failed messages in the DLQ and logs them,
 * while also delegating them to the DLQService for persistence.
 */
@Component
public class DeadLetterQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterQueueListener.class);
    private final DLQService dlqService;

    public DeadLetterQueueListener(DLQService dlqService) {
        this.dlqService = dlqService;
    }

    /**
     * Handles incoming messages from the Dead Letter Queue (DLQ).
     * This method listens for messages that have failed processing,
     * logs them, and stores them in the database for further analysis.
     *
     * @param message the failed message received from the DLQ
     */
    @JmsListener(destination = "DLQ.GROUP_EVENTS_QUEUE", containerFactory = "jmsListenerContainerFactory")
    public void receiveDeadLetterMessage(Map<String, Object> message) {
        logger.error("Dead Letter Queue received failed message: {}", message);

        // Store the failed message in the database
        dlqService.saveFailedMessage(message);
    }
}

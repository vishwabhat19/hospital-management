package com.hospital.management.consumer;

import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;

/**
 * Listener component for handling invalid operations in the group events queue.
 * This class listens to JMS messages that do not match expected operations (CREATE or DELETE)
 * and redirects them to the Dead Letter Queue (DLQ).
 */
@Component
public class InvalidOperationListener {

    private static final Logger logger = LoggerFactory.getLogger(InvalidOperationListener.class);

    private final JmsTemplate jmsTemplate;

    public InvalidOperationListener(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Handles messages with invalid operations in the group events queue.
     * The method listens for messages with an operation that is not "CREATE" or "DELETE",
     * logs an error, sends the message to the Dead Letter Queue (DLQ), and
     * responds with an error message if a reply queue is specified.
     *
     * @param message the incoming message with an invalid operation
     * @throws JMSException if an error occurs while processing the message
     */
    @JmsListener(destination = GROUP_EVENTS_QUEUE, containerFactory = "jmsListenerContainerFactory", concurrency = "3-5", selector = "operation NOT IN ('CREATE', 'DELETE')")
    public void handleInvalidOperation(Map<String, Object> message) throws JMSException {
        String operation = (String) message.get("operation");
        String replyToQueue = (String) message.get("JMSReplyTo");
        logger.error("Unknown operation: {}. Sending to DLQ.", operation);
        // Send the message to DLQ
        jmsTemplate.convertAndSend("DLQ.GROUP_EVENTS_QUEUE", message);
        Map<String, Object> responseMessage = new HashMap<>();
        responseMessage.put("groupId", message.get("groupId"));
        responseMessage.put("status", HttpStatus.BAD_REQUEST.value());
        responseMessage.put("message", "Invalid Operation: "+operation);
        if (replyToQueue != null) {
            jmsTemplate.convertAndSend(replyToQueue, responseMessage);
        }
    }
}


package com.hospital.management.consumer;

import org.springframework.stereotype.Component;
import com.hospital.management.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;

/**
 * Listener component for processing messages related to group deletion.
 * This class listens to JMS messages on the specified queue and invokes the GroupService
 * to delete a group based on the received message payload.
 */
@Component
public class DeleteGroupListener {
    private static final Logger logger = LoggerFactory.getLogger(DeleteGroupListener.class);
    private final GroupService groupService;
    private final JmsTemplate jmsTemplate;

    public DeleteGroupListener(GroupService groupService, JmsTemplate jmsTemplate) {
        this.groupService = groupService;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Handles incoming JMS messages for group deletion.
     * The method listens to the specified queue with a selector for "DELETE" operations,
     * extracts relevant data, invokes the GroupService to delete a group,
     * and sends a response message if a reply queue is specified.
     *
     * @param message the incoming message containing group deletion details
     */
    @JmsListener(destination = GROUP_EVENTS_QUEUE, containerFactory = "jmsListenerContainerFactory", selector = "operation = 'DELETE'", concurrency = "3-5")
    public void receiveDeleteMessage(Map<String, Object> message) {
        String operation = (String) message.get("operation");
         logger.info("Received DELETE message: {}", message);
            String replyToQueue = (String) message.get("JMSReplyTo");

            Map<String, Object> responseMessage = new HashMap<>();
            responseMessage.put("groupId", message.get("groupId"));

            try {
                ResponseEntity<?> responseEntity = groupService.deleteGroup(message);
                responseMessage.put("status", responseEntity.getStatusCodeValue());
                responseMessage.put("message", responseEntity.getBody());
            } catch (Exception e) {
                responseMessage.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                responseMessage.put("message", e.getMessage());
            }

            if (replyToQueue != null) {
                jmsTemplate.convertAndSend(replyToQueue, responseMessage);
            }
    }
}


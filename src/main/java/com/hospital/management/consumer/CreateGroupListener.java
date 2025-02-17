package com.hospital.management.consumer;

import com.hospital.management.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;

/**
 * Listener component for processing messages related to group creation.
 * This class listens to JMS messages on the specified queue and invokes the GroupService
 * to create a group based on the received message payload.
 */
@Component
public class CreateGroupListener {
    private static final Logger logger = LoggerFactory.getLogger(CreateGroupListener.class);
    private final GroupService groupService;
    private final JmsTemplate jmsTemplate;

    public CreateGroupListener(GroupService groupService, JmsTemplate jmsTemplate) {
        this.groupService = groupService;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Handles incoming JMS messages for group creation.
     * The method listens to the specified queue with a selector for "CREATE" operations,
     * extracts relevant data, invokes the GroupService to create a group,
     * and sends a response message if a reply queue is specified.
     *
     * @param message the incoming message containing group creation details
     * @throws Exception if any error occurs while processing the message
     */
    @JmsListener(destination = GROUP_EVENTS_QUEUE, selector = "operation = 'CREATE'", containerFactory = "jmsListenerContainerFactory")
    public void receiveCreateMessage(Map<String, Object> message) throws Exception {
             logger.info("Received CREATE message: {}", message);
                String replyToQueue = (String) message.get("JMSReplyTo");

                Map<String, Object> responseMessage = new HashMap<>();
                responseMessage.put("groupId", message.get("groupId"));

                ResponseEntity<?> responseEntity = groupService.createGroup(message);
                responseMessage.put("status", responseEntity.getStatusCodeValue());
                responseMessage.put("message", responseEntity.getBody());

                if (replyToQueue != null) {
                    jmsTemplate.convertAndSend(replyToQueue, responseMessage);
                }
    }
}

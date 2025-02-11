package com.hospital.management.producer;

import jakarta.jms.MapMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;

/**
 * Service class responsible for producing and sending group event messages
 * to the specified JMS queue.
 */
@Service
public class GroupMessageProducer {
    private final JmsTemplate jmsTemplate;

    public GroupMessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Sends a group event message to the JMS queue.
     *
     * @param groupId       the unique identifier of the group
     * @param parentGroupId the identifier of the parent group
     * @param groupName     the name of the group
     * @param timestamp     the timestamp of the event
     * @param operation     the type of operation (e.g., CREATE, DELETE)
     * @param replyToQueue  the queue for receiving replies (optional)
     */
    public void sendGroupEvent(String groupId, String parentGroupId, String groupName, String timestamp, String operation, String replyToQueue) {
        jmsTemplate.send(GROUP_EVENTS_QUEUE, session -> {
            MapMessage message = session.createMapMessage();

            message.setStringProperty("operation", operation);

            message.setString("groupId", groupId);
            message.setString("parentGroupId", parentGroupId);
            message.setString("groupName", groupName);
            message.setString("timestamp", timestamp);
            message.setString("operation", operation);

            if (replyToQueue != null) {
                message.setString("JMSReplyTo", replyToQueue);
            }

            return message;
        });
    }
}

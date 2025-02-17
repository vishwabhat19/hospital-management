package com.hospital.management.consumer;

import com.hospital.management.service.GroupService;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.autoconfigure.jms.AutoConfigureEmbeddedActiveMQ;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hospital.management.service.GroupService;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jms.AutoConfigureEmbeddedActiveMQ;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureEmbeddedActiveMQ
class CreateGroupListenerTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @MockBean
    private GroupService groupService;

    @Test
    void testReceiveCreateMessage() {
        // Given
        when(groupService.createGroup(any(Map.class))).thenReturn(ResponseEntity.ok("Group Created"));

        jmsListenerEndpointRegistry.start();

        // When
        jmsTemplate.send(GROUP_EVENTS_QUEUE, session -> {
            MapMessage message = session.createMapMessage();
            message.setStringProperty("operation", "CREATE");
            message.setString("groupId", "G001");
            message.setString("parentGroupId", "PG001");
            message.setString("groupName", "Cardiology");
            message.setString("timestamp", "2025-02-09T18:32:00");
            message.setString("JMSReplyTo", "REPLY_QUEUE");

            return message;
        });

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(groupService, times(1)).createGroup(any(Map.class));
        });

        jmsListenerEndpointRegistry.stop();
    }
}
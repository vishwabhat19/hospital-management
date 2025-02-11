package com.hospital.management.consumer;

import com.hospital.management.service.GroupService;
import jakarta.jms.MapMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableJms
class CreateGroupListenerTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @MockBean
    private GroupService groupService;

    @SpyBean
    private CreateGroupListener createGroupListener;

    @BeforeEach
    void setup() {
        Mockito.reset(groupService, createGroupListener);
    }

    @Test
    void testReceiveCreateMessage() {
        // Given
        when(groupService.createGroup(any())).thenReturn(ResponseEntity.ok("Group Created"));

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
            verify(createGroupListener, atLeast(1)).receiveCreateMessage(any());
        });
    }
}

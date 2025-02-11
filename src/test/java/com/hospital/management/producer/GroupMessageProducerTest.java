package com.hospital.management.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static com.hospital.management.util.AppConstants.GROUP_EVENTS_QUEUE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMessageProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private GroupMessageProducer groupMessageProducer;

    @Test
    void testSendGroupEvent() {
        //Given /When
        groupMessageProducer.sendGroupEvent("1", "P1", "Test Group", "2025-02-10T10:00:00", "CREATE", "REPLY_QUEUE");

        //Then
        verify(jmsTemplate, times(1)).send(eq(GROUP_EVENTS_QUEUE), any());
    }
}

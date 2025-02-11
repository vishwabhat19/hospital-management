package com.hospital.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.dto.GroupRequestDTO;
import com.hospital.management.producer.GroupMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private MockMvc mockMvc; // Remove final and initialize in @BeforeEach

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private GroupMessageProducer groupMessageProducer;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void testCreateGroup() throws Exception {
        //Given
        GroupRequestDTO requestDTO = new GroupRequestDTO("1", "P1", "Test Group", "2025-02-10T10:00:00", "CREATE");
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 200);
        responseMap.put("message", "Group Created");
        when(jmsTemplate.receiveAndConvert(anyString())).thenReturn(responseMap);

        //When /Then
        mockMvc.perform(post("/hospital/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Group Created"));
    }
}

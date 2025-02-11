package com.hospital.management.controller;

import com.hospital.management.dto.GroupRequestDTO;
import com.hospital.management.producer.GroupMessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller class for handling group management operations in the hospital system.
 * Provides endpoints for creating or deleting groups through JMS messaging.
 */
@RestController
@RequestMapping("hospital")
@Tag(name = "Group Management", description = "APIs for managing groups in the hospital system")
public class GroupController {

    private final JmsTemplate jmsTemplate;
    private final GroupMessageProducer groupMessageProducer;

    public GroupController(JmsTemplate jmsTemplate, GroupMessageProducer groupMessageProducer) {
        this.jmsTemplate = jmsTemplate;
        this.groupMessageProducer = groupMessageProducer;
    }

    /**
     * Creates or deletes a group based on the provided {@link GroupRequestDTO}.
     * The operation type must be either 'CREATE' or 'DELETE'.
     * Sends a group event to the JMS system and waits for a response.
     *
     * @param requestDTO the DTO containing the group details and operation to perform
     * @return a {@link ResponseEntity} containing the status and message of the operation
     * @throws Exception if there is an error processing the JMS message
     */
    @Operation(
            summary = "Create or Delete a Group",
            description = "Sends a group event to JMS. Supports only 'CREATE' or 'DELETE' operations.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Group request payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GroupRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Create Group Example",
                                            value = """
                                            {
                                              "groupId": "G001",
                                              "parentGroupId": "PG001",
                                              "groupName": "Cardiology",
                                              "timestamp": "2025-02-09T18:32:00",
                                              "operation": "CREATE"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Delete Group Example",
                                            value = """
                                            {
                                              "groupId": "G001",
                                              "parentGroupId": "PG001",
                                              "timestamp": "2025-02-09T18:32:00",
                                              "operation": "DELETE"
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group event processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/group")
    public ResponseEntity<String> createOrDeleteGroup(@RequestBody @Valid GroupRequestDTO requestDTO) throws Exception{
            String replyQueue = "GROUP_RESPONSE_QUEUE";
            // Send request with reply-to queue
            groupMessageProducer.sendGroupEvent(requestDTO.getGroupId(), requestDTO.getParentGroupId(), requestDTO.getGroupName(), requestDTO.getTimestamp(), requestDTO.getOperation(), replyQueue);

            // Receive response from the response queue
            Map<String, Object> response = (Map<String, Object>) jmsTemplate.receiveAndConvert(replyQueue);
            if (response != null) {
                int status = (int)response.get("status");
                String message = (String) response.get("message");

                return ResponseEntity.status(status).body(message);
            }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No response from consumer.");
    }

}

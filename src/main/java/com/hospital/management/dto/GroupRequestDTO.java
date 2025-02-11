package com.hospital.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating or deleting a group")
@AllArgsConstructor
public class GroupRequestDTO {

    @NotNull(message = "Group ID is required")
    @Schema(description = "Unique ID of the group", example = "G001", required = true)
    private String groupId;

    @NotNull(message = "Parent Group ID is required")
    @Schema(description = "ID of the parent group", example = "PG001", required = true)
    private String parentGroupId;

    @Schema(description = "Name of the group", example = "Cardiology")
    private String groupName;

    @NotNull(message = "Timestamp is required")
    @Schema(description = "Timestamp of the event", example = "2025-02-09T18:32:00", required = true)
    private String timestamp;

    @NotNull(message = "Operation is required")
    @Schema(description = "Operation type (CREATE or DELETE)", example = "DELETE", allowableValues = {"CREATE", "DELETE"}, required = true)
    private String operation;
}

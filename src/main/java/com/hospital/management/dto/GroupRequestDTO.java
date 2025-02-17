package com.hospital.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating or deleting a group")
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

    public GroupRequestDTO(String groupId, String parentGroupId, String groupName, String timestamp, String operation) {
        this.groupId = groupId;
        this.parentGroupId = parentGroupId;
        this.groupName = groupName;
        this.timestamp = timestamp;
        this.operation = operation;
    }

    public @NotNull(message = "Group ID is required") String getGroupId() {
        return groupId;
    }

    public @NotNull(message = "Parent Group ID is required") String getParentGroupId() {
        return parentGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public @NotNull(message = "Timestamp is required") String getTimestamp() {
        return timestamp;
    }

    public @NotNull(message = "Operation is required") String getOperation() {
        return operation;
    }

    public void setGroupId(@NotNull(message = "Group ID is required") String groupId) {
        this.groupId = groupId;
    }

    public void setParentGroupId(@NotNull(message = "Parent Group ID is required") String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setTimestamp(@NotNull(message = "Timestamp is required") String timestamp) {
        this.timestamp = timestamp;
    }

    public void setOperation(@NotNull(message = "Operation is required") String operation) {
        this.operation = operation;
    }
}

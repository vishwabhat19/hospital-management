package com.hospital.management.service;

import com.hospital.management.entity.Group;
import com.hospital.management.entity.GroupActivityLog;
import com.hospital.management.repository.GroupActivityLogRepository;
import com.hospital.management.repository.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing groups within the hospital management system.
 * Provides functionality to create and delete groups, ensuring data consistency
 * and logging group activities.
 */
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupActivityLogRepository groupActivityLogRepository;

    public GroupService(GroupRepository groupRepository, GroupActivityLogRepository groupActivityLogRepository) {
        this.groupRepository = groupRepository;
        this.groupActivityLogRepository = groupActivityLogRepository;
    }

    /**
     * Creates a new group based on the provided message data.
     * If the group already exists, it returns a conflict response.
     *
     * @param message the message containing group details
     * @return ResponseEntity with the status and message of the operation
     */
    @Transactional
    public ResponseEntity<String> createGroup(Map<String, Object> message) {
        String groupId = (String) message.get("groupId");
        String parentGroupId = (String) message.get("parentGroupId");
        String groupName = (String) message.get("groupName");

        if (groupRepository.existsByGroupIdAndParentGroupId(groupId, parentGroupId)) {
            System.out.println("hey");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Group with given Group ID and Parent Group ID already exists.");
        }

        //If the timestamp has not been provided then use the LocalDateTime.now() as the current timestamp
        LocalDateTime timestamp = Optional.ofNullable((String) message.get("timestamp"))
                .map(ts -> LocalDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME))
                .orElse(LocalDateTime.now());

        Group group = new Group(groupId, parentGroupId, groupName, timestamp);
        groupRepository.save(group);
        groupActivityLogRepository.save(new GroupActivityLog(groupId, parentGroupId, "CREATE"));

        return ResponseEntity.ok("Group created successfully.");
    }

    /**
     * Deletes an existing group based on the provided message data.
     * Ensures that the delete timestamp is valid before performing the deletion.
     *
     * @param message the message containing group details
     * @return ResponseEntity with the status and message of the operation
     */
    @Transactional
    public ResponseEntity<String> deleteGroup(Map<String, Object> message) {
        String groupId = (String) message.get("groupId");
        String parentGroupId = (String) message.get("parentGroupId");
        //If the timestamp has not been provided then use the LocalDateTime.now() as the current timestamp
        LocalDateTime deletedTimestamp = Optional.ofNullable((String) message.get("timestamp"))
                .map(ts -> LocalDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME))
                .orElse(LocalDateTime.now());
        if (!groupRepository.existsByGroupIdAndParentGroupId(groupId, parentGroupId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group with given Group ID and Parent Group ID does not exist.");
        }

        Group group = groupRepository.findByGroupIdAndParentGroupId(groupId, parentGroupId);
        LocalDateTime createdTimestamp = group.getCreatedTimestamp();
        if (createdTimestamp.isAfter(deletedTimestamp) || createdTimestamp.isEqual(deletedTimestamp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deleted Timestamp cannot be equal to or before Created Timestamp.");
        }

        groupRepository.deleteByGroupIdAndParentGroupId(groupId, parentGroupId);
        groupActivityLogRepository.save(new GroupActivityLog(groupId, parentGroupId, "DELETE"));

        return ResponseEntity.ok("Group deleted successfully.");
    }
}
package com.hospital.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_activity_log")
@Getter
@Setter
@NoArgsConstructor
public class GroupActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "parent_group_id", nullable = false)
    private String parentGroupId;

    @Column(name = "action_type", nullable = false)
    private String actionType; // "CREATE" or "DELETE"

    @Column(name = "action_timestamp")
    private LocalDateTime actionTimestamp = LocalDateTime.now();

    public GroupActivityLog(String groupId, String parentGroupId, String actionType) {
        this.groupId = groupId;
        this.parentGroupId = parentGroupId;
        this.actionType = actionType;
    }
}

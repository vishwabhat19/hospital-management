package com.hospital.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "parent_group_id")
    private String parentGroupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "created_timestamp")
    private LocalDateTime createdTimestamp;

    public Group(String groupId, String parentGroupId, String groupName, LocalDateTime createdTimestamp) {
        this.groupId = groupId;
        this.parentGroupId = parentGroupId;
        this.groupName = groupName;
        this.createdTimestamp = createdTimestamp;
    }
}

package com.hospital.management.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMessage {

    private String groupId;
    private String parentGroupId;
    private String operation;
    private String timestamp;

}

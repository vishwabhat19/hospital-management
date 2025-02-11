package com.hospital.management.repository;

import com.hospital.management.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByGroupIdAndParentGroupId(String groupId, String parentGroupId);
    void deleteByGroupIdAndParentGroupId(String groupId, String parentGroupId);
    Group findByGroupIdAndParentGroupId(String groupId, String parentGroupId);
}

package com.hospital.management.repository;

import com.hospital.management.entity.GroupActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupActivityLogRepository extends JpaRepository<GroupActivityLog, Long> {
}

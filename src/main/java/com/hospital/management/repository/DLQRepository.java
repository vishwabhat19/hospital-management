package com.hospital.management.repository;

import com.hospital.management.entity.DLQMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and manipulating {@link DLQMessage} entities.
 * Extends {@link JpaRepository} to provide CRUD operations.
 */
@Repository
public interface DLQRepository extends JpaRepository<DLQMessage, Long> {

}

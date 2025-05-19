package com.kafka.consumer.springbootconsumer.repository;

import com.kafka.consumer.springbootconsumer.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
} 
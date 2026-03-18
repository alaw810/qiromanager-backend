package com.qiromanager.qiromanager_backend.infrastructure.audit;

import com.qiromanager.qiromanager_backend.domain.audit.AuditLog;
import com.qiromanager.qiromanager_backend.domain.audit.AuditLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLog, Long>, AuditLogRepository {
}

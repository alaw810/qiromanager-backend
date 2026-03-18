package com.qiromanager.qiromanager_backend.domain.audit;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);
}

package com.qiromanager.qiromanager_backend.application.audit;

import com.qiromanager.qiromanager_backend.domain.audit.AuditAction;
import com.qiromanager.qiromanager_backend.domain.audit.AuditLog;
import com.qiromanager.qiromanager_backend.domain.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String entityType, Long entityId, AuditAction action, String details) {
        String performedBy = resolveCurrentUser();
        AuditLog entry = AuditLog.create(entityType, entityId, action, performedBy, details);
        auditLogRepository.save(entry);
        log.debug("Audit: {} on {} ID={} by {}", action, entityType, entityId, performedBy);
    }

    private String resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "system";
        }
        return auth.getName();
    }
}

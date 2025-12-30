package com.qiromanager.qiromanager_backend.domain.treatment.events;

import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TreatmentSessionCreatedEvent extends ApplicationEvent {

    private final TreatmentSession treatmentSession;

    public TreatmentSessionCreatedEvent(Object source, TreatmentSession treatmentSession) {
        super(source);
        this.treatmentSession = treatmentSession;
    }
}
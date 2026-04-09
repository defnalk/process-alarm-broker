package com.defneertugrul.alarmer.broadcast;

import com.defneertugrul.alarmer.api.AlarmResponse;
import com.defneertugrul.alarmer.domain.AlarmEvent;
import com.defneertugrul.alarmer.persistence.AlarmRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Pushes accepted (non-suppressed) alarms to /topic/alarms.
 * Suppressed events are ignored — they exist for audit, not for downstream UIs.
 */
@Component
public class AlarmEventListener {

    private final SimpMessagingTemplate messaging;
    private final AlarmRepository repo;

    public AlarmEventListener(SimpMessagingTemplate messaging, AlarmRepository repo) {
        this.messaging = messaging;
        this.repo = repo;
    }

    @EventListener
    public void onEvent(AlarmEvent event) {
        if (event instanceof AlarmEvent.AlarmAccepted accepted) {
            repo.findById(accepted.alarmId())
                    .map(AlarmResponse::from)
                    .ifPresent(r -> messaging.convertAndSend("/topic/alarms", r));
        }
    }
}

package az.qrfood.backend.mail.dto.events;

import az.qrfood.backend.mail.dto.EventType;
import az.qrfood.backend.mail.entity.EmailTemplate;
import java.util.Map;

public record EmailEvent(
        String email,
        String locale,
        EmailTemplate template,
        EventType event,
        Map<String, Object> map) {
}
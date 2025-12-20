package az.qrfood.backend.mail.dto;

import java.util.Map;

public record MagicLinkCreationEvent(String email, String locale, String link, Map<String, Object> map) { }

package az.qrfood.backend.mail.dto;

import java.util.Map;

public record UserRegisteredEvent(String email, String locale, Map<String, Object> map) { }

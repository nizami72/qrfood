package az.qrfood.backend.mail.dto;

import jakarta.validation.constraints.NotNull;

public class SubscriptionUpdateRequest {
    @NotNull(message = "Type is required")
    private SubscriptionType type; // Spring сам сконвертирует строку "WEEKLY_DIGEST" в Enum

    @NotNull(message = "Enabled flag is required")
    private Boolean enabled; // true = подписаться, false = отписаться

    // Getters & Setters
    public SubscriptionType getType() { return type; }
    public void setType(SubscriptionType type) { this.type = type; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
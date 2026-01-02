package az.qrfood.backend.mail.dto;

import lombok.Getter;

@Getter
public enum SubscriptionType {
    PRODUCT_UPDATES(1),
    WEEKLY_DIGEST(2),
    SUCCESS_TIPS(4),
    PROMOTIONS(8),
    ONBOARDING_NUDGE(16);

    private final int value;

    SubscriptionType(int value) {
        this.value = value;
    }

    // Метод для проверки, содержит ли маска эту подписку
    public static boolean hasSubscription(int mask, SubscriptionType type) {
        return (mask & type.value) != 0;
    }

    // Метод для добавления подписки к маске
    public static int addSubscription(int mask, SubscriptionType type) {
        return mask | type.value;
    }

    // Метод для удаления подписки из маски
    public static int removeSubscription(int mask, SubscriptionType type) {
        return mask & ~type.value;
    }
}
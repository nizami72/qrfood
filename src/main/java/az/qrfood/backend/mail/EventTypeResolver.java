package az.qrfood.backend.mail;

import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.mail.dto.EventType;

public class EventTypeResolver {

    public static EventType resolveByOnboardingStatus(OnboardingStatus onboardingStatus) {
        return switch (onboardingStatus) {
            case REGISTERED -> EventType.CREATE_EATERY;
            case EATERY_CREATED -> EventType.CREATE_CATEGORIES;
            case CATEGORY_CREATED -> EventType.CREATE_DISHES;
            case DISH_CREATED -> EventType.CREATE_TABLES;
            case TABLES_READY -> EventType.ADD_STAFF;
            case USER_ADDED -> EventType.ADD_DEPARTMENT;
        };
    }
}
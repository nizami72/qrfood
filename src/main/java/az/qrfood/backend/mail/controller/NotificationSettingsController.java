package az.qrfood.backend.mail.controller;

import az.qrfood.backend.constant.ApiRoutes;
import az.qrfood.backend.mail.dto.SubscriptionUpdateRequest;
import az.qrfood.backend.mail.service.NotificationSettingsService;
import az.qrfood.backend.mail.dto.SubscriptionType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(ApiRoutes.EMAIL_SUBSCRIPTION)
@RequiredArgsConstructor
@Tag(name = "Notification Settings", description = "Управление подписками на email-рассылки")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationService;

    /**
     * Получить текущие настройки подписок
     */
    @GetMapping
    public ResponseEntity<List<SubscriptionType>> getSubscriptions(
            @AuthenticationPrincipal UserDetails currentUser) { // Или ваш класс UserPrincipal
        
        Long userId = Long.parseLong(currentUser.getUsername());
        List<SubscriptionType> activeSubs = notificationService.getActiveSubscriptions(userId);
        return ResponseEntity.ok(activeSubs);
    }

    /**
     * Подписаться или отписаться от конкретного типа уведомлений
     */
    @PatchMapping
    public ResponseEntity<Void> updateSubscription(
            @RequestBody @Valid SubscriptionUpdateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long userId = Long.parseLong(currentUser.getUsername());
        notificationService.updateSubscription(
                userId, 
                request.getType(), 
                request.getEnabled()
        );
        return ResponseEntity.noContent().build(); // 204 No Content - стандарт для успешного апдейта без возврата тела
    }
}
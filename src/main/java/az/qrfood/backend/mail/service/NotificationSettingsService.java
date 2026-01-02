package az.qrfood.backend.mail.service;

import az.qrfood.backend.mail.dto.SubscriptionType;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok для инъекции зависимостей
public class NotificationSettingsService {

    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void updateSubscription(Long userId, SubscriptionType type, boolean enable) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        int currentMask = userProfile.getEmailSubscription();
        int newMask;

        if (enable) {
            // Побитовое OR: Включаем бит, сохраняя остальные
            newMask = currentMask | type.getValue();
        } else {
            // Побитовое AND с инверсией: Выключаем бит, сохраняя остальные
            newMask = currentMask & ~type.getValue();
        }

        userProfile.setEmailSubscription(newMask);
        userProfileRepository.save(userProfile);
        
        // Тут можно добавить логирование: log.info("User {} changed sub {} to {}", userId, type, enable);
    }
    
    // Метод для получения списка активных подписок (для отображения на UI)
    public List<SubscriptionType> getActiveSubscriptions(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<SubscriptionType> active = new ArrayList<>();
        int mask = userProfile.getEmailSubscription();

        for (SubscriptionType type : SubscriptionType.values()) {
            if ((mask & type.getValue()) != 0) {
                active.add(type);
            }
        }
        return active;
    }
}
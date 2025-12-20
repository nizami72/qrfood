package az.qrfood.backend.eatery.service;

import az.qrfood.backend.eatery.dto.OnboardingStatus;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.repository.EateryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EateryLifecycleService {

    private final EateryRepository eateryRepository;

    /**
     * Пытается продвинуть статус ресторана вперед.
     * Вызывается из DishService, TableService, StaffService.
     */
    @Transactional
    public void tryPromoteStatus(Long eateryId, OnboardingStatus targetStatus) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found"));

        OnboardingStatus current = eatery.getOnboardingStatus();

        // Логика "Храповика" (Ratchet Logic): Двигаемся только вперед.
        // ordinal() возвращает номер в enum (0, 1, 2...)
        if (targetStatus.ordinal() > current.ordinal()) {
            
            // Здесь можно добавить дополнительные проверки (Double Check)
            // Например, действительно ли существуют блюда, если мы ставим MENU_STARTED
            
            eatery.setOnboardingStatus(targetStatus);
            eateryRepository.save(eatery);
            
            // Тут можно кинуть ивент для аналитики или отправки письма "Поздравляем!"
        }
    }

    public void promoteStatus(Long eateryId, OnboardingStatus targetStatus) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new EntityNotFoundException("Eatery not found"));
        eatery.setOnboardingStatus(targetStatus);
        eateryRepository.save(eatery);
    }
}
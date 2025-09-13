package az.qrfood.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDto(Long categoryId, Long EateryId, String nameAz, String nameEn, String nameRu, String image) {}
package az.qrfood.backend.eatery;

import java.util.List;

public record Eatery(String name, String address, String [] phones, double geoLat, double geoLng) {}

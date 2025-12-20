package az.qrfood.backend.selenium.dto;

import java.util.List;

@lombok.Data
public class Eatery{
	private String address;
	private String name;
	private double geoLat;
    private double geoLng;
	private List<String> phones;
	private int id;
}
package az.qrfood.backend.selenium.dto;

import java.util.List;

public class Eatery{
	private Object geoLng;
	private String address;
	private String name;
	private Object geoLat;
	private List<String> phones;
	private int id;

	public Object getGeoLng(){
		return geoLng;
	}

	public String getAddress(){
		return address;
	}

	public String getName(){
		return name;
	}

	public Object getGeoLat(){
		return geoLat;
	}

	public List<String> getPhones(){
		return phones;
	}

	public int getId(){
		return id;
	}
}
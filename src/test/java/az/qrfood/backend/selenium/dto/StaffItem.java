package az.qrfood.backend.selenium.dto;

import java.util.List;

public class StaffItem{
	private String password;
	private List<String> roles;
	private Profile profile;
	private String email;

	public String getPassword(){
		return password;
	}

	public List<String> getRoles(){
		return roles;
	}

	public Profile getProfile(){
		return profile;
	}

	public String getEmail(){
		return email;
	}
}
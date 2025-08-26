package az.qrfood.backend.selenium.dto;

import java.util.List;

public class Testov{
	private Eatery eatery;
	private List<TableItem> tables;
	private List<StaffItem> staff;

	public Eatery getEatery(){
		return eatery;
	}

	public List<TableItem> getTables(){
		return tables;
	}

	public List<StaffItem> getStaff(){
		return staff;
	}
}
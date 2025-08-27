package az.qrfood.backend.selenium.dto;

import java.util.List;

public class Testov{
	private Eatery eatery;
	private List<Table> tables;
	private List<StaffItem> staff;
	private List<CategoriesItem> categories;

	public Eatery getEatery(){
		return eatery;
	}

	public List<Table> getTables(){
		return tables;
	}

	public List<StaffItem> getStaff(){
		return staff;
	}

	public List<CategoriesItem> getCategories(){
		return categories;
	}
}
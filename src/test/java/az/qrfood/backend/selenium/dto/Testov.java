package az.qrfood.backend.selenium.dto;

import az.qrfood.backend.category.dto.CategoryDto;
import java.util.List;

public class Testov{
	private Eatery eatery;
	private List<Table> tables;
	private List<StaffItem> staff;
	private List<CategoryDto> categories;

	public Eatery getEatery(){
		return eatery;
	}

	public List<Table> getTables(){
		return tables;
	}

	public List<StaffItem> getStaff(){
		return staff;
	}

	public List<az.qrfood.backend.category.dto.CategoryDto> getCategories(){
		return categories;
	}

    public StaffItem getEateryAdmin(){
        return getStaff().stream().filter(s -> s.getRoles().contains("EATERY_ADMIN")).findFirst()
                .orElseThrow();
    }
}
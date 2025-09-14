package az.qrfood.backend.selenium.dto;

import az.qrfood.backend.dish.dto.DishDto;
import java.util.List;

public class CategoriesItem{
	private String nameRu;
	private String image;
	private int eateryId;
	private List<DishDto> dishes;
	private String nameAz;
	private String nameEn;
	private int categoryId;

	public String getNameRu(){
		return nameRu;
	}

	public String getImage(){
		return image;
	}

	public int getEateryId(){
		return eateryId;
	}

	public List<DishDto> getDishes(){
		return dishes;
	}

	public String getNameAz(){
		return nameAz;
	}

	public String getNameEn(){
		return nameEn;
	}

	public int getCategoryId(){
		return categoryId;
	}
}
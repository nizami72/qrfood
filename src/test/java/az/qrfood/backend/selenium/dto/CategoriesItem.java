package az.qrfood.backend.selenium.dto;

import java.util.List;

public class CategoriesItem{
	private String nameRu;
	private String image;
	private int eateryId;
	private List<DishesItem> dishes;
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

	public List<DishesItem> getDishes(){
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
package az.qrfood.backend.selenium.dto;

public class DishesItem{
	private String nameRu;
	private String descriptionAz;
	private String descriptionEn;
	private String image;
	private boolean isAvailable;
	private Object price;
	private int dishId;
	private String nameAz;
	private String nameEn;
	private String descriptionRu;
	private int categoryId;

	public String getNameRu(){
		return nameRu;
	}

	public String getDescriptionAz(){
		return descriptionAz;
	}

	public String getDescriptionEn(){
		return descriptionEn;
	}

	public String getImage(){
		return image;
	}

	public boolean isIsAvailable(){
		return isAvailable;
	}

	public Object getPrice(){
		return price;
	}

	public int getDishId(){
		return dishId;
	}

	public String getNameAz(){
		return nameAz;
	}

	public String getNameEn(){
		return nameEn;
	}

	public String getDescriptionRu(){
		return descriptionRu;
	}

	public int getCategoryId(){
		return categoryId;
	}
}

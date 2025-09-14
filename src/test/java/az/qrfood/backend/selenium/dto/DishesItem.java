package az.qrfood.backend.selenium.dto;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class DishesItem {

    private int dishId;
    private int categoryId;

    private String nameRu;
    private String nameAz;
    private String nameEn;

    private String descriptionAz;
    private String descriptionEn;
    private String descriptionRu;

    private BigDecimal price;
    private String image;
    private boolean isAvailable;

	public boolean isIsAvailable(){
		return isAvailable;
	}
}

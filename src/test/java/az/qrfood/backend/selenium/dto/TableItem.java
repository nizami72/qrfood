package az.qrfood.backend.selenium.dto;

public class TableItem {

	private String number;
	private String note;
	private int eateryId;
	private QrCodeDto qrCodeDto;
	private int id;
	private int seats;
	private String status;

	public String getNumber(){
		return number;
	}

	public String getNote(){
		return note;
	}

	public int getEateryId(){
		return eateryId;
	}

	public QrCodeDto getQrCodeDto(){
		return qrCodeDto;
	}

	public int getId(){
		return id;
	}

	public int getSeats(){
		return seats;
	}

	public String getStatus(){
		return status;
	}
}

package az.qrfood.backend.selenium.dto;

public class Table {

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

	@Override
	public String toString() {
		return "{\"Table\":\n{"
				+ "        \"number\":\"" + number + "\""
				+ ",         \"note\":\"" + note + "\""
				+ ",         \"eateryId\":\"" + eateryId + "\""
				+ ",         \"id\":\"" + id + "\""
				+ ",         \"seats\":\"" + seats + "\""
				+ ",         \"status\":\"" + status + "\""
				+ "\n}\n}";
	}
}

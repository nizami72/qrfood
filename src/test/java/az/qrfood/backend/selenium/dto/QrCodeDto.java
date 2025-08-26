package az.qrfood.backend.selenium.dto;

public class QrCodeDto{
	private Object qrCodeAsBytes;
	private int id;
	private String validFrom;
	private String content;
	private String validTo;

	public Object getQrCodeAsBytes(){
		return qrCodeAsBytes;
	}

	public int getId(){
		return id;
	}

	public String getValidFrom(){
		return validFrom;
	}

	public String getContent(){
		return content;
	}

	public String getValidTo(){
		return validTo;
	}
}

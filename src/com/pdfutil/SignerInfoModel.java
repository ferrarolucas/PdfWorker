package com.pdfutil;

public class SignerInfoModel {

	public String signerName;
	public String signerText;
	public String signingTime;
	
	public SignerInfoModel() { }
	
	public ErrorModel validate() {
		
		if(this.signerName == null && this.signerName != "") {
			return new ErrorModel("O nome do assinante não pode ser nulo ou vazio");
		}

		if(this.signerText == null && this.signerText != "") {
			return new ErrorModel("O texto da assinatura não pode ser nulo ou vazio");
		}

		if(this.signingTime == null && this.signingTime != "") {
			return new ErrorModel("O horário da assinatura não pode ser nulo ou vazio");
		}
		
		return null;
	}
	
}

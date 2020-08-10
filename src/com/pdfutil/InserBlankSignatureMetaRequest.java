package com.pdfutil;

import java.io.Serializable;

public class InserBlankSignatureMetaRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String pdfB64;

	public SignatureFieldModel signatureField;

	public SignerInfoModel signerInfo;

	public InfoModel[] metadata;
	
	public boolean createXmp;

	public InserBlankSignatureMetaRequest() { }

	public ErrorModel validate() {

		if(this.pdfB64 == null && this.pdfB64 != "") {
			return new ErrorModel("O PDF n�o pode ser nulo ou vazio.");
		}

		if(this.signatureField == null) {
			return new ErrorModel("O campo da assinatura n�o pode ser nulo.");
		}

		ErrorModel signatureFieldValidationError = this.signatureField.validateForCreate();
		if(signatureFieldValidationError != null) {
			return signatureFieldValidationError;
		}

		if(this.signerInfo == null) {
			return new ErrorModel("As informa��es do assinante n�o podem ser nulas.");
		}

		ErrorModel signerInfoValidationError = this.signerInfo.validate();
		if(signerInfoValidationError != null) {
			return signerInfoValidationError;
		}

		return null;
	}

}
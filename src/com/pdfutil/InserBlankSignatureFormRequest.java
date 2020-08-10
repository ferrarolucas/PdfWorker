package com.pdfutil;

import java.io.Serializable;

public class InserBlankSignatureFormRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String pdfB64;

	public SignatureFieldModel signatureField;

	public SignerInfoModel signerInfo;

	public FormFieldModel[] fieldsToBlock;
	
	public boolean blocksWholeForm;

	public InserBlankSignatureFormRequest() { }

	public ErrorModel validate() {

		if(this.pdfB64 == null && this.pdfB64 != "") {
			return new ErrorModel("O PDF não pode ser nulo ou vazio.");
		}

		if(this.signatureField == null) {
			return new ErrorModel("O campo da assinatura não pode ser nulo.");
		}

		ErrorModel signatureFieldValidationError = this.signatureField.validateForInsertValue();
		if(signatureFieldValidationError != null) {
			return signatureFieldValidationError;
		}

		if(this.signerInfo == null) {
			return new ErrorModel("As informações do assinante não podem ser nulas.");
		}

		ErrorModel signerInfoValidationError = this.signerInfo.validate();
		if(signerInfoValidationError != null) {
			return signerInfoValidationError;
		}

		return null;
	}

}

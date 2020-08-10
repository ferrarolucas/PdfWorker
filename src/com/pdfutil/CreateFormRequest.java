package com.pdfutil;

import java.io.Serializable;

public class CreateFormRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String pdfB64;
	
	public FormFieldModel[] fields;
	
	public SignatureFieldModel[] signatureFields;

	public CreateFormRequest() { }
	
	public ErrorModel validate() {
		
		if(this.pdfB64 == null && this.pdfB64 != "") {
			return new ErrorModel("O PDF não pode ser nulo ou vazio.");
		}
			
		ErrorModel fieldsValidationError = validateFields(this.fields);
		if(fieldsValidationError != null) {
			return fieldsValidationError;
		}
		
		ErrorModel signatureFieldsValidationError = validateFields(this.signatureFields);
		if(signatureFieldsValidationError != null) {
			return signatureFieldsValidationError;
		}
		
		return null;
	}

	private ErrorModel validateFields(FieldModel[] fields) {
		if(fields != null && fields.length > 0) {
			for(int i = 0 ; i < fields.length ; i++) {
				ErrorModel signatureFieldResult = fields[i].validateForCreate();
				if(signatureFieldResult != null) {
					return signatureFieldResult;
				}
			}
		}
		return null;
	}
	
}

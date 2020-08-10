package com.pdfutil;

public class SignatureFieldModel extends FieldModel {
	
	public String logoB64;

	public SignatureFieldModel() { }
	
	public ErrorModel validateForCreate() {
		if(this.name == null && this.name != "") {
			return new ErrorModel("Todo campo de assinatura deve ter um nome.");
		}

		ErrorModel positionResult = position.validate();
		if(positionResult != null) {
			return positionResult;
		}
		
		return null;
	}
	
	public ErrorModel validateForInsertValue() {
		
		if(this.name == null && this.name != "") {
			return new ErrorModel("Todo campo de assinatura deve ter um nome.");
		}

		return null;
	}
	
}

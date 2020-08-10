package com.pdfutil;

public class FieldModel {

	public String name;

	public RectangleModel position;

	public FieldModel() { }	
	
	public ErrorModel validateForCreate() {
		if(this.name == null && this.name != "") {
			return new ErrorModel("Todo campo deve ter um nome.");
		}

		ErrorModel positionResult = position.validate();
		if(positionResult != null) {
			return positionResult;
		}
		
		return null;
	}
	
	public ErrorModel validateForInsertValue() {
		
		if(this.name == null && this.name != "") {
			return new ErrorModel("Todo campo deve ter um nome.");
		}

		return null;
	}
	
}

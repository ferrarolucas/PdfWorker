package com.pdfutil;

public class InfoModel {

	public String name;
	
	public String value;
	
	public InfoModel() { }	
	
	public ErrorModel validateForCreate() {
		if(this.name == null && this.name != "") {
			return new ErrorModel("Todo campo deve ter um nome.");
		}

		return null;
	}
	
}

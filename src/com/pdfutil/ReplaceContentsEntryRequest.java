package com.pdfutil;

import java.io.Serializable;

public class ReplaceContentsEntryRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String blankSignedPdfB64;

	public String signatureFieldName;

	public String p7sB64;

	public ReplaceContentsEntryRequest() { }
	
	public ErrorModel validate() {
		
		if(this.blankSignedPdfB64 == null && this.blankSignedPdfB64 != "") {
			return new ErrorModel("O PDF com a assiantura em branco não pode ser nulo ou vazio.");
		}

		if(this.signatureFieldName == null) {
			return new ErrorModel("O nome do campo da assinatura não pode ser nulo ou vazio.");
		}

		if(this.p7sB64 == null && this.p7sB64 != "") {
			return new ErrorModel("A assiantura (p7s) não pode ser nulo ou vazio.");
		}

		return null;
		
	}

}

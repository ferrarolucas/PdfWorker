package com.pdfutil.asn1;

public class CertsAndSignerInfos {

	private SuperSet certificates;
	private SuperSet signerInfos;
	
	public CertsAndSignerInfos() {
	}

	public SuperSet getCertificates() {
		return certificates;
	}

	public void setCertificates(SuperSet certificates) {
		this.certificates = certificates;
	}

	public SuperSet getSignerInfos() {
		return signerInfos;
	}

	public void setSignerInfos(SuperSet signerInfos) {
		this.signerInfos = signerInfos;
	}
	
}

package com.pdfutil.asn1;

public class CertsCrlsAndSignerInfos {

	private SuperSet certificates;
	private SuperSet signerInfos;
	private SuperSet crls;
	
	public CertsCrlsAndSignerInfos() {
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

	public SuperSet getCrls() {
		return crls;
	}

	public void setCrls(SuperSet crls) {
		this.crls = crls;
	}
	
}

package com.pdfutil.asn1;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class CleanedSignature {

	private byte[] signature;
	private ArrayList<X509Certificate> extractedCerts;
	private ArrayList<X509CRL> extractedCrls;
	
	public CleanedSignature( byte[] signature, ArrayList<X509Certificate> extractedCerts, ArrayList<X509CRL> extractedCrls) {
		this.setSignature(signature);
		this.setExtractedCerts(extractedCerts);
		this.setExtractedCrls(extractedCrls);
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public ArrayList<X509Certificate> getExtractedCerts() {
		return extractedCerts;
	}

	public void setExtractedCerts(ArrayList<X509Certificate> extractedCerts) {
		this.extractedCerts = extractedCerts;
	}

	public ArrayList<X509CRL> getExtractedCrls() {
		return extractedCrls;
	}

	public void setExtractedCrls(ArrayList<X509CRL> extractedCrls) {
		this.extractedCrls = extractedCrls;
	}
			
}

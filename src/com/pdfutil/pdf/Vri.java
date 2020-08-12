package com.pdfutil.pdf;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfName;

public class Vri {
	private PdfName key;
	private ArrayList<X509Certificate> certs;
	private ArrayList<X509CRL> crls;

	private PdfArray certArray;
	private PdfArray crlArray;
	
	public Vri(PdfName key, ArrayList<X509Certificate> certs, ArrayList<X509CRL> crls) 
	{ 
		this.key = key;
		this.certs = certs;
		this.crls = crls;
	}
	
	public PdfName getKey() {
		return key;
	}
	
	public void setKey(PdfName key) {
		this.key = key;
	}
	
	public ArrayList<X509Certificate> getCerts() {
		return certs;
	}
	
	public void setCerts(ArrayList<X509Certificate> certs) {
		this.certs = certs;
	}
	
	public ArrayList<X509CRL> getCrls() {
		return crls;
	}
	
	public void setCrls(ArrayList<X509CRL> crls) {
		this.crls = crls;
	}
	
	public PdfArray getCertArray() {
		return certArray;
	}
	
	public void setCertArray(PdfArray certArray) {
		this.certArray = certArray;
	}
	
	public PdfArray getCrlArray() {
		return crlArray;
	}
	
	public void setCrlArray(PdfArray crlArray) {
		this.crlArray = crlArray;
	}
}

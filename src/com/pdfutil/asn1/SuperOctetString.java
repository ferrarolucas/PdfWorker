package com.pdfutil.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;

public class SuperOctetString {

	private DEROctetString derObj;
	private BEROctetString berObj;
	private ASN1OctetString asn1Obj;

	public SuperOctetString(ASN1Encodable obj) {
		if(obj instanceof DEROctetString) {
			this.derObj = (DEROctetString)obj;
		}else if(obj instanceof BEROctetString) {
			this.berObj = (BEROctetString)obj;
		}else if(obj instanceof ASN1OctetString) {
			this.asn1Obj = (ASN1OctetString)obj;
		}
	}
	
	public static boolean isSuperOctetString(ASN1Encodable obj) {
		if(obj instanceof DEROctetString) {
			return true;
		} else if(obj instanceof BEROctetString) {
			return true;
		}else if(obj instanceof ASN1OctetString) {
			return true;
		} else {
			return false;
		}
	}

	public byte[] getOctets() {
		if(this.derObj != null) {
			return this.derObj.getOctets();
		} else if(this.berObj != null) {
			return this.berObj.getOctets();
		} else if(this.asn1Obj != null) {
			return this.asn1Obj.getOctets();
		}

		return null;
	}

	
	
	
}

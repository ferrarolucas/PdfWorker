package com.pdfutil.asn1;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DLBitString;

public class SuperBitString {

	public SuperBitString() { }
	
	public static boolean isSuperBitString(ASN1Encodable obj) {
		if(obj instanceof DLBitString) {
			return true;
		} else if(obj instanceof DERBitString) {
			return true;
		}else if(obj instanceof ASN1BitString) {
			return true;
		} else {
			return false;
		}
	}
	
}

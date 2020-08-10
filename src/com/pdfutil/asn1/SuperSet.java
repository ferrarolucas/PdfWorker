package com.pdfutil.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DLSet;


public class SuperSet {

	private DLSet dlObj;
	private DERSet derObj;
	private BERSet berObj;
	private ASN1Set asn1Obj;

	public SuperSet(ASN1Encodable obj) {
		if(obj instanceof DLSet) {
			this.dlObj = (DLSet)obj;
		} else if(obj instanceof DERSet) {
			this.derObj = (DERSet)obj;
		}else if(obj instanceof BERSet) {
			this.berObj = (BERSet)obj;
		}else if(obj instanceof ASN1Set) {
			this.asn1Obj = (ASN1Set)obj;
		}
	}

	public SuperSet(ASN1EncodableVector newSequenceVector, Asn1Type type) {
		switch (type) {
		case DL:
			this.dlObj = new DLSet(newSequenceVector);
			break;
		case DER:
			this.derObj = new DERSet(newSequenceVector);
			break;
		case BER:
			this.berObj = new BERSet(newSequenceVector);
			break;
		case ASN1:
			// TODO erro
			break;
		default:
			break;
		}
	}

	public ASN1Encodable getObjectAt(int i) {
		if(this.dlObj != null) {
			return this.dlObj.getObjectAt(i);
		} else if(this.derObj != null) {
			return this.derObj.getObjectAt(i);
		} else if(this.berObj != null) {
			return this.berObj.getObjectAt(i);
		} else if(this.asn1Obj != null) {
			return this.asn1Obj.getObjectAt(i);
		}

		return null;
	}

	public int size() {
		if(this.dlObj != null) {
			return this.dlObj.size();
		} else if(this.derObj != null) {
			return this.derObj.size();
		} else if(this.berObj != null) {
			return this.berObj.size();
		} else if(this.asn1Obj != null) {
			return this.asn1Obj.size();
		}

		return 0;
	}

	public ASN1Encodable getObj() {
		if(this.dlObj != null) {
			return this.dlObj;
		} else if(this.derObj != null) {
			return this.derObj;
		} else if(this.berObj != null) {
			return this.berObj;
		} else if(this.asn1Obj != null) {
			return this.asn1Obj;
		}

		return null;
	}
	
}

package com.pdfutil.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;

public class SuperSequence {

	private DLSequence dlObj;
	private DERSequence derObj;
	private BERSequence berObj;
	private ASN1Sequence asn1Obj;

	public SuperSequence(ASN1Encodable obj) {
		if(obj instanceof DLSequence) {
			this.dlObj = (DLSequence)obj;
		} else if(obj instanceof DERSequence) {
			this.derObj = (DERSequence)obj;
		}else if(obj instanceof BERSequence) {
			this.berObj = (BERSequence)obj;
		}else if(obj instanceof ASN1Sequence) {
			this.asn1Obj = (ASN1Sequence)obj;
		}
	}
	
	public static boolean isSuperSequence(ASN1Encodable obj) {
		if(obj instanceof DLSequence) {
			return true;
		} else if(obj instanceof DERSequence) {
			return true;
		}else if(obj instanceof BERSequence) {
			return true;
		}else if(obj instanceof ASN1Sequence) {
			return true;
		}
		else {
			return false;
		}
	}

	public SuperSequence(ASN1EncodableVector newSequenceVector, Asn1Type type) {
		switch (type) {
		case DL:
			this.dlObj = new DLSequence(newSequenceVector);
			break;
		case DER:
			this.derObj = new DERSequence(newSequenceVector);
			break;
		case BER:
			this.berObj = new BERSequence(newSequenceVector);
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

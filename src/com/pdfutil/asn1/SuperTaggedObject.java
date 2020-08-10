package com.pdfutil.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLTaggedObject;

public class SuperTaggedObject {

	private DLTaggedObject dlObj;
	private DERTaggedObject derObj;
	private BERTaggedObject berObj;
	private ASN1TaggedObject asn1Obj;

	public SuperTaggedObject(ASN1Encodable obj) {
		if(obj instanceof DLTaggedObject) {
			this.dlObj = (DLTaggedObject)obj;
		} else if(obj instanceof DERTaggedObject) {
			this.derObj = (DERTaggedObject)obj;
		}else if(obj instanceof BERTaggedObject) {
			this.berObj = (BERTaggedObject)obj;
		}else if(obj instanceof ASN1TaggedObject) {
			this.asn1Obj = (ASN1TaggedObject)obj;
		}
	}

	public ASN1Encodable getObjectParser(int i, boolean b) throws Exception {
		if(this.dlObj != null) {
			return this.dlObj.getObjectParser(i, b);
		} else if(this.derObj != null) {
			return this.derObj.getObjectParser(i, b);
		} else if(this.berObj != null) {
			return this.berObj.getObjectParser(i, b);
		} else if(this.asn1Obj != null) {
			return this.asn1Obj.getObjectParser(i, b);
		}

		return null;
	}

	public int getTagNo() {
		if(this.dlObj != null) {
			return this.dlObj.getTagNo();
		} else if(this.derObj != null) {
			return this.derObj.getTagNo();
		} else if(this.berObj != null) {
			return this.berObj.getTagNo();
		} else if(this.asn1Obj != null) {
			return this.asn1Obj.getTagNo();
		}

		return 0;
	}

}

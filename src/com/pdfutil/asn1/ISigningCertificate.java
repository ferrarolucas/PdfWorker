package com.pdfutil.asn1;

import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.bouncycastle.asn1.cms.Attribute;

public interface ISigningCertificate {

	void decode(Attribute attribute);
	
	X509Certificate selectCertificate(ArrayList<X509Certificate> certificates);
}

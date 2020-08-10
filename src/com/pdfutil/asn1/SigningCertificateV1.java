package com.pdfutil.asn1;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

import com.pdfutil.util.IHash;

public class SigningCertificateV1 implements ISigningCertificate {

	protected String identifier;
    protected byte[] certHash;
    private final String Sha1AlgorithmId = PKCSObjectIdentifiers.sha1WithRSAEncryption.getId();
	
    public SigningCertificateV1() {
    	this.identifier = PKCSObjectIdentifiers.id_aa_signingCertificate.getId();
	}
    
    @Override
    public void decode(Attribute attribute)
    {
    	SuperSet derSet = new SuperSet(attribute.getAttrValues());
		SuperSequence signingCertificateSequence = new SuperSequence(derSet.getObjectAt(0));
		SuperSequence certsSequence = new SuperSequence(signingCertificateSequence.getObjectAt(0));
		
		// pegar só a primeira identidade - assinante
		SuperSequence certIdSequence = new SuperSequence(certsSequence.getObjectAt(0));
		SuperOctetString derBitString = new SuperOctetString(certIdSequence.getObjectAt(0));
		this.certHash = derBitString.getOctets();
    }

    @Override
    public X509Certificate selectCertificate(ArrayList<X509Certificate> certificates) {
    	if (certificates != null)
    	{
    		for(int i = 0 ; i < certificates.size() ; i++) {
    			X509Certificate currentCertificate = certificates.get(i);
    			byte[] currentCertHash = IHash.calculateCertificateHash(currentCertificate, this.Sha1AlgorithmId);
    			if(Arrays.equals(currentCertHash, this.certHash)) {
    				return currentCertificate;
    			}
    		}
    	}
    	return null;
    }

}

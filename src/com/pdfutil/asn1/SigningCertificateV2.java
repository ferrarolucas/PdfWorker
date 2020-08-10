package com.pdfutil.asn1;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import com.pdfutil.util.IHash;

public class SigningCertificateV2 implements ISigningCertificate {

	protected String identifier;
	protected byte[] certHash;
	private String hashAlgorithm;

	public SigningCertificateV2() {
		this.identifier = PKCSObjectIdentifiers.id_aa_signingCertificateV2.getId();
	}

	@Override
	public void decode(Attribute attribute)
	{
		SuperSet derSet = new SuperSet(attribute.getAttrValues());
		SuperSequence signingCertificateSequence = new SuperSequence(derSet.getObjectAt(0));
		SuperSequence certsSequence = new SuperSequence(signingCertificateSequence.getObjectAt(0));
		
		// pegar só a primeira identidade - assinante
		SuperSequence certIdSequence = new SuperSequence(certsSequence.getObjectAt(0));
		
		if(SuperOctetString.isSuperOctetString(certIdSequence.getObjectAt(0))) {
			SuperOctetString derBitString = new SuperOctetString(certIdSequence.getObjectAt(0));
			this.certHash = derBitString.getOctets();
			this.hashAlgorithm = "SHA-256"; // default
		} else {
			AlgorithmIdentifier algorithmIdentifier = (AlgorithmIdentifier) certIdSequence.getObjectAt(0);
			this.hashAlgorithm = algorithmIdentifier.getAlgorithm().getId();
			SuperOctetString derBitString = new SuperOctetString(certIdSequence.getObjectAt(1));
			this.certHash = derBitString.getOctets();
		}
	}

	@Override
	public X509Certificate selectCertificate(ArrayList<X509Certificate> certificates) {
		if (certificates != null)
		{
			for(int i = 0 ; i < certificates.size() ; i++) {
				X509Certificate currentCertificate = certificates.get(i);
				byte[] currentCertHash = IHash.calculateCertificateHash(currentCertificate, this.hashAlgorithm);
				if(Arrays.equals(currentCertHash, this.certHash)) {
					return currentCertificate;
				}
			}
		}
		return null;
	}

}

package com.pdfutil.asn1;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.cms.CMSSignedData;

public class P7sCleaner {

	public static byte[] cleanSignatureCertificates(byte[] inputBytes) throws Exception
    {
        SuperSequence signedDataSequence = Asn1Util.getSignedDataSequence(inputBytes);
        SuperSet hashAlgorithm = new SuperSet(signedDataSequence.getObjectAt(1));
        ContentInfo contentInfo = Asn1Util.getContentInfo(signedDataSequence);

        CertsAndSignerInfos certsAndSignerInfo = Asn1Util.getCertsCrlsSignerInfosAsn1(signedDataSequence);
        SuperSet oldCerts = certsAndSignerInfo.getCertificates();
        
        SignedData oldSignedData = new SignedData((ASN1Set)hashAlgorithm.getObj(), contentInfo, (ASN1Set)oldCerts.getObj(), null, (ASN1Set)certsAndSignerInfo.getSignerInfos().getObj());
        ASN1Set signerInfos = oldSignedData.getSignerInfos();
        ASN1Sequence signerInfoSequence = (ASN1Sequence)signerInfos.getObjectAt(0);
        
        @SuppressWarnings("deprecation")
		SignerInfo signerInfo = new SignerInfo(signerInfoSequence);

        List<Attribute> attributes = Asn1Util.getAttributes(signerInfo.getAuthenticatedAttributes());

        ISigningCertificate signingCertificate = Asn1Util.getSigningCertificate(attributes);

        ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
        if (oldCerts != null && oldCerts.size() > 0 )
        {
        	CertificateFactory factory = CertificateFactory.getInstance("X.509");
            for (int i = 0; i < oldCerts.size(); i++)
            {
            	byte[] certBytes = ((ASN1Object) oldCerts.getObjectAt(i)).getEncoded();
            	ByteArrayInputStream stream = new ByteArrayInputStream(certBytes);
            	X509Certificate cert = (X509Certificate) factory.generateCertificate(stream);
            	
                certificates.add(cert);
            }
        }
        else
        {
            throw new Exception("Não foram encontrados certificados no p7s.");
        }

        X509Certificate signerCertificate = signingCertificate.selectCertificate(certificates);

        ASN1Set certsSet = Asn1Util.createBerSetForCertificates(signerCertificate);
        SignedData newSignedData = new SignedData(oldSignedData.getDigestAlgorithms(), oldSignedData.getEncapContentInfo(), certsSet, null, oldSignedData.getSignerInfos());
        ContentInfo newContentInfo = new ContentInfo(CMSObjectIdentifiers.signedData, newSignedData);
        CMSSignedData newCmsSignedData = new CMSSignedData(newContentInfo);
		return newCmsSignedData.getEncoded();
		
    }
	
}

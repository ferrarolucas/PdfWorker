package com.pdfutil.asn1;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class Asn1Util {

	public static ISigningCertificate getSigningCertificate(List<Attribute> attributes) throws Exception
	{
		String signingCertificateOid = PKCSObjectIdentifiers.id_aa_signingCertificate.getId();
		String signingCertificateV2Oid = PKCSObjectIdentifiers.id_aa_signingCertificateV2.getId();

		ISigningCertificate signinCertificate = null;
		Attribute currentAttribute = null;
		for(int i = 0 ; i < attributes.size() ; i++) {
			currentAttribute = attributes.get(i);
			if(currentAttribute.getAttrType().getId().compareTo(signingCertificateOid) == 0) {
				signinCertificate = new SigningCertificateV1();
				signinCertificate.decode(currentAttribute);
				return signinCertificate;
			} else if(currentAttribute.getAttrType().getId().compareTo(signingCertificateV2Oid) == 0) {
				signinCertificate = new SigningCertificateV2();
				signinCertificate.decode(currentAttribute);
				return signinCertificate;
			}
		}

		throw new Exception("N�o foi poss�vel encontrar um atributo de identifica��o do assinante");
	}

	public static List<Attribute> getAttributes(ASN1Set attributesTable)
	{
		ArrayList<Attribute> attributesFound = new ArrayList<Attribute>();
		if (attributesTable != null)
		{
			for (int i = 0; i < attributesTable.size(); i++)
			{
				ASN1Sequence attributeSequence = (ASN1Sequence)attributesTable.getObjectAt(i);
				attributesFound.add(new Attribute((ASN1ObjectIdentifier)attributeSequence.getObjectAt(0), (ASN1Set)attributeSequence.getObjectAt(1)));
			}
		}
		return attributesFound;
	}

	public static SuperSequence getSignedDataSequence(byte[] inputBytes) throws Exception
	{
		SuperSequence derSequence = new SuperSequence(DERSequence.fromByteArray(inputBytes));
		SuperTaggedObject derTagged = new SuperTaggedObject(derSequence.getObjectAt(1));
		SuperSequence signedDataSequence = new SuperSequence(derTagged.getObjectParser(0, true));

		return signedDataSequence;
	}

	public static CertsCrlsAndSignerInfos getCertsCrlsSignerInfosAsn1(SuperSequence signedDataSequence) throws Exception
	{
		CertsCrlsAndSignerInfos certCrlsAndSignerInfos = new CertsCrlsAndSignerInfos();
		int qntyFieldsSignedData = signedDataSequence.size();
		if (qntyFieldsSignedData == 6)
		{
			SuperTaggedObject derTaggedCert = new SuperTaggedObject(signedDataSequence.getObjectAt(3));
			SuperSequence certsSequence = new SuperSequence(derTaggedCert.getObjectParser(0, true));
			certCrlsAndSignerInfos.setCertificates(getDerSetFromDerSequence(certsSequence));
			
			SuperTaggedObject derTaggedCrl = new SuperTaggedObject(signedDataSequence.getObjectAt(4));
			SuperSequence crlsSequence = new SuperSequence(derTaggedCrl.getObjectParser(0, true));
			certCrlsAndSignerInfos.setCrls(getDerSetFromDerSequence(crlsSequence));
			
			certCrlsAndSignerInfos.setSignerInfos(new SuperSet(signedDataSequence.getObjectAt(5)));
		}
		else if (qntyFieldsSignedData == 5)
		{
			SuperTaggedObject derTaggedCertOrCrl = new SuperTaggedObject(signedDataSequence.getObjectAt(3));
			if (derTaggedCertOrCrl.getTagNo() == 0)
			{
				SuperSequence certsSequence = new SuperSequence(derTaggedCertOrCrl.getObjectParser(0, true));
				SuperSet certsSet = getDerSetFromDerSequence(certsSequence);
				certCrlsAndSignerInfos.setCertificates(certsSet);
			}
			else {
				SuperSequence crlsSequence = new SuperSequence(derTaggedCertOrCrl.getObjectParser(1, true));
				SuperSet crlsSet = getDerSetFromDerSequence(crlsSequence);
				certCrlsAndSignerInfos.setCrls(crlsSet);
			}

			certCrlsAndSignerInfos.setSignerInfos(new SuperSet(signedDataSequence.getObjectAt(4)));
		}
		else
		{
			certCrlsAndSignerInfos.setSignerInfos(new SuperSet(signedDataSequence.getObjectAt(3)));
		}

		return certCrlsAndSignerInfos;
	}

	public static ASN1Set createBerSetForCertificates(X509Certificate certificate) throws Exception
	{
		ASN1EncodableVector vector = new ASN1EncodableVector();
		vector.add(DERSequence.fromByteArray(certificate.getEncoded()));
		return new BERSet(vector);
	}

	public static ContentInfo getContentInfo(SuperSequence signedDataSequence) throws Exception
	{
		SuperSequence conteudoSequence = new SuperSequence(signedDataSequence.getObjectAt(2));
		ContentInfo conteudo = null;
		if (conteudoSequence.size() == 2)
		{
			SuperTaggedObject derTaggedContent = new SuperTaggedObject(conteudoSequence.getObjectAt(1));
			SuperOctetString octetString = new SuperOctetString(derTaggedContent.getObjectParser(0, true));
			conteudo = new ContentInfo((ASN1ObjectIdentifier)conteudoSequence.getObjectAt(0), new DEROctetString(octetString.getOctets()));
		}
		else
			conteudo = new ContentInfo((ASN1ObjectIdentifier)conteudoSequence.getObjectAt(0), null);

		return conteudo;
	}

	public static SuperSet getDerSetFromDerSequence(SuperSequence certificadosSequence)
	{
		ASN1EncodableVector vector = new ASN1EncodableVector();
		boolean needOuterSequence = certificadosSequence.size() % 3 == 0 &&
				SuperSequence.isSuperSequence(certificadosSequence.getObjectAt(0)) &&
				SuperSequence.isSuperSequence(certificadosSequence.getObjectAt(1)) &&
				SuperBitString.isSuperBitString(certificadosSequence.getObjectAt(2));

		SuperSequence newCertsSequence = null;
		if (needOuterSequence)
		{
			int numberOfCerts = certificadosSequence.size() / 3;
			ASN1EncodableVector newSequenceVector = new ASN1EncodableVector();
			for (int i = 0; i < numberOfCerts; i++)
			{
				ASN1EncodableVector outerVector = new ASN1EncodableVector();
				outerVector.add(certificadosSequence.getObjectAt(0 + i));
				outerVector.add(certificadosSequence.getObjectAt(1 + i));
				outerVector.add(certificadosSequence.getObjectAt(2 + i));
				DERSequence outerSequence = new DERSequence(outerVector);
				newSequenceVector.add(outerSequence);
			}
			newCertsSequence = new SuperSequence(newSequenceVector, Asn1Type.DL);
		}
		else
		{
			newCertsSequence = certificadosSequence;
		}

		for(int i = 0 ; i < newCertsSequence.size() ; i++) {
			SuperSequence certSequence = new SuperSequence(newCertsSequence.getObjectAt(i));
			vector.add(certSequence.getObj());
		}

		return new SuperSet(vector, Asn1Type.DER);
	}

	public static ArrayList<X509CRL> extractCrls(SuperSet crlsSequence) throws Exception {
		ArrayList<X509CRL> crls = new ArrayList<X509CRL>();
        if (crlsSequence != null && crlsSequence.size() > 0 )
        {
        	CertificateFactory factory = CertificateFactory.getInstance("X.509");
            for (int i = 0; i < crlsSequence.size(); i++)
            {
            	byte[] crlBytes = ((ASN1Object) crlsSequence.getObjectAt(i)).getEncoded();
            	ByteArrayInputStream stream = new ByteArrayInputStream(crlBytes);
            	X509CRL crl = (X509CRL) factory.generateCRL(stream);
            	
                crls.add(crl);
            }
        }
		return null;
	}

}

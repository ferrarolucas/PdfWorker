package com.pdfutil.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class IHash {

	private MessageDigest messageDigest;

	public IHash(String hashAlgorithm) {

		if(hashAlgorithm == "2.16.840.1.101.3.4.2.1") {
			hashAlgorithm = "SHA-256";
		} else if(hashAlgorithm == "1.3.14.3.2.26") {
			hashAlgorithm = "SHA-1";
		} else if(hashAlgorithm == "2.16.840.1.101.3.4.2.3") {
			hashAlgorithm = "SHA-512";
		}

		try {
			this.messageDigest = MessageDigest.getInstance(hashAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Erro ao instanciar o mecanismo de hash.");
			e.printStackTrace();
		}
	}

	public byte[] getHash(byte[] contentToHash) {
		this.messageDigest.update(contentToHash);
		return this.messageDigest.digest();
	}

	public static byte[] calculateCertificateHash(X509Certificate certificate, String hashAlgorithm)
	{
		IHash digester = new IHash(hashAlgorithm);
		byte[] certBytes = null;

		try {
			certBytes = certificate.getEncoded();
			return digester.getHash(certBytes);
		} catch (Exception e) {
			System.err.println("Erro ao obtero hash do certificado");
			e.printStackTrace();
		}


		return null;
	}
	
	public static byte[] calculateHash(byte[] content, String hashAlgorithm)
	{
		IHash digester = new IHash(hashAlgorithm);
		return digester.getHash(content);
	}

}

package com.pdfutil.pdf;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.bouncycastle.util.encoders.Hex;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfIndirectReference;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.pdfutil.util.IHash;

public class Dss {

	private PdfReader reader;
	private PdfStamper stamper;

	private PdfArray certs;
	private PdfArray crls;
	private PdfDictionary vris;

	public Dss(PdfReader reader, PdfStamper stamper) {
		this.reader = reader;
		this.stamper = stamper;
	}

	public void Generate(ArrayList<Vri> vris) throws Exception {
		GenerateVris(vris);
		GenerateDss();
	}

	private void GenerateDss() throws Exception {

		var dss = new PdfDictionary();
		dss.put(PdfName.TYPE, PdfName.DSS);
		dss.put(PdfName.CERTS, GetPdfIndirectReference(this.certs));
		dss.put(PdfName.CRLS, GetPdfIndirectReference(this.crls));

		dss.put(PdfName.VRI, this.stamper.getWriter().addToBody(this.vris, false).getIndirectReference());
		this.reader.getCatalog().put(PdfName.DSS, this.stamper.getWriter().addToBody(dss, false).getIndirectReference());
	}

	private void GenerateVris(ArrayList<Vri> vris) throws Exception {

		this.vris = new PdfDictionary();
		this.certs = new PdfArray();
		this.crls = new PdfArray();

		for (int i = 0 ; i < vris.size() ; i++) {
			Vri vri = vris.get(i);

			var vriDicEntries = new PdfDictionary();
			vriDicEntries.put(PdfName.TYPE, PdfName.VRI);

			// CERTS
			var certsBytes = new ArrayList<byte[]>();
			for(int j = 0 ; j < vri.getCerts().size() ; j++) {
				X509Certificate cert = vri.getCerts().get(j);
				certsBytes.add(cert.getEncoded());
			}
			vri.setCertArray(getPdfArray(certsBytes));
			vriDicEntries.put(PdfName.CERT, GetPdfIndirectReference(vri.getCertArray()));

			// CRLS
			var crlsBytes = new ArrayList<byte[]>();
			for(int j = 0 ; j < vri.getCrls().size() ; j++) {
				X509CRL crl = vri.getCrls().get(j);
				crlsBytes.add(crl.getEncoded());
			}
			vri.setCrlArray(getPdfArray(crlsBytes));
			vriDicEntries.put(PdfName.CRL, GetPdfIndirectReference(vri.getCrlArray()));

			this.vris.put(vri.getKey(), this.stamper.getWriter().addToBody(vriDicEntries, false).getIndirectReference());
		}

	}

	private PdfArray getPdfArray(ArrayList<byte[]> artifacts) throws Exception {
		PdfArray array = new PdfArray();
		for (int i = 0 ; i < artifacts.size() ; i++) {
			byte[] artifact = artifacts.get(i);

			PdfStream stream = new PdfStream(artifact);
			PdfIndirectObject indirectObj = this.stamper.getWriter().addToBody(stream, false);
			array.add(indirectObj.getIndirectReference());
		}

		return array;
	}

	public static PdfName generateVriKey(byte[] artifactBytes) throws Exception {
		var artifactHash = IHash.calculateHash(artifactBytes, "SHA-1");
		var vriKeyBytes = Hex.encode(artifactHash);
		var vriKey = new String(vriKeyBytes, "UTF-8");
		var vriPdfName = new PdfName(vriKey.toUpperCase());
		return vriPdfName;
	}

	private PdfIndirectReference GetPdfIndirectReference(PdfArray array) throws Exception {
		PdfIndirectObject indirectObject = this.stamper.getWriter().addToBody(array, false);
		return indirectObject.getIndirectReference();
	}

}

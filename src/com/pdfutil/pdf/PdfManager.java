package com.pdfutil.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSigLockDictionary;
import com.itextpdf.text.pdf.PdfSigLockDictionary.LockAction;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.TextField;
import com.itextpdf.text.xml.xmp.XmpWriter;
import com.pdfutil.CreateFormResponse;
import com.pdfutil.FormFieldModel;
import com.pdfutil.InfoModel;
import com.pdfutil.InserBlankSignatureResponse;
import com.pdfutil.ReplaceContentsEntryResponse;
import com.pdfutil.SignatureFieldModel;
import com.pdfutil.asn1.P7sCleaner;
import com.pdfutil.util.IHash;

public class PdfManager {

	private static int CONTENTS_SIZE_ENTRY = 8000;

	public static byte[] fillForm(byte[] pdfBytes, FormFieldModel[] fieldsToFill) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(pdfBytes);

		PdfReader reader = new PdfReader(input);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, output);

		AcroFields form = stamper.getAcroFields(); 
		Map<String, Item> fields = form.getFields();

		for(int i = 0 ; i < fieldsToFill.length ; i++) {
			FormFieldModel currentField = fieldsToFill[i];

			if(!form.doesSignatureFieldExist(currentField.name) && fields.containsKey(currentField.name)) {

				if(currentField.value != null && currentField.value != "") {
					form.setField(currentField.name, currentField.value);
				}
				form.setFieldProperty(currentField.name, "setfflags", TextField.READ_ONLY, null);
			}
		}

		stamper.close();
		return output.toByteArray();
	}

	public static byte[] copy(byte[] pdfBytes) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(pdfBytes);
		PdfReader inputReader = new PdfReader(input);
		
		Document document = new Document();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfCopy copy = new PdfCopy(document, output);
		document.open();
		
		int pageQnty = inputReader.getNumberOfPages();
		for(int i = 1; i <= pageQnty ; i++) {
	
			copy.addPage(copy.getImportedPage(inputReader, i));
		}
		
		document.close();
		return output.toByteArray();
	}
	
	public static byte[] insertHeader(byte[] pdfBytes, InfoModel[] metaData, boolean createXmp) throws Exception {
		
		ByteArrayInputStream input = new ByteArrayInputStream(pdfBytes);
		PdfReader reader = new PdfReader(input);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, output);
		
		Map<String, String> info = reader.getInfo();
		
		for(int i = 0 ; i < metaData.length ; i++) {
			InfoModel currentData = metaData[i];
			String value = currentData.value == null ? currentData.name : currentData.value;
			info.put(currentData.name, value);
		}
		stamper.setMoreInfo(info);
		
		if(createXmp) {
			stamper.getWriter().createXmpMetadata();
		}
		
		stamper.close();
		return output.toByteArray();
	}
	
	public static byte[] insertMetadata(byte[] pdfBytes, InfoModel[] metaData) throws Exception {
		
		ByteArrayInputStream input = new ByteArrayInputStream(pdfBytes);

		PdfReader reader = new PdfReader(input);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, output);
		
		PdfDictionary page = reader.getPageN(1);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmpWriter xmp = new XmpWriter(baos, new PdfDictionary());
      
        
		for(int i = 0 ; i < metaData.length ; i++) {
			InfoModel currentData = metaData[i];
			String value = currentData.value == null ? currentData.name : currentData.value;
			xmp.addDocInfoProperty(currentData.name, value);
		}

		xmp.close();
		PdfIndirectObject ref = stamper.getWriter().addToBody(new PdfStream(baos.toByteArray()));
        page.put(PdfName.METADATA, ref.getIndirectReference());
		
		stamper.close();
		reader.close();
		return output.toByteArray();
	}
	
	public static InserBlankSignatureResponse insertBlankSignature(
			byte[] pdfBytes, String signatureFieldName, String signerName, 
			String signerText, String signingTime, String logoB64, 
			FormFieldModel[] fieldsToBlock, boolean blocksWholeForm
			) throws Exception {

		ByteArrayInputStream input = new ByteArrayInputStream(pdfBytes);

		PdfReader reader = new PdfReader(input);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper	stamper = PdfStamper.createSignature(reader, output, '\0', null, true);

		AcroFields form = stamper.getAcroFields(); 
		Map<String, Item> fields = form.getFields();

		PdfSigLockDictionary fieldLock = null;

		if(fieldsToBlock != null && fieldsToBlock.length > 0) {
			String[] fieldToBlockNames = new String[fieldsToBlock.length];
			for(int i = 0 ; i < fieldsToBlock.length ; i++) {
				FormFieldModel currentField = fieldsToBlock[i];

				if(!form.doesSignatureFieldExist(currentField.name) && fields.containsKey(currentField.name)) {
					fieldToBlockNames[i] = currentField.name;
				}
			}
			fieldLock = new PdfSigLockDictionary(LockAction.INCLUDE, fieldToBlockNames);
		}
		
		if(blocksWholeForm) {
			fieldLock = new PdfSigLockDictionary(LockAction.ALL);
		}

		stamper.getWriter().setPdfVersion(PdfWriter.PDF_VERSION_1_7);

		PdfDictionary cadesExtensionDictionary = new PdfDictionary();
		cadesExtensionDictionary.put(PdfName.BASEVERSION, new PdfName("1.7"));
		cadesExtensionDictionary.put(PdfName.EXTENSIONLEVEL, new PdfNumber(2));

		PdfDictionary esicExtensionDictionary = new PdfDictionary();
		esicExtensionDictionary.put(new PdfName("ESIC"), cadesExtensionDictionary);
		reader.getCatalog().put(new PdfName("Extensions"), esicExtensionDictionary);

		PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
		appearance.setVisibleSignature(signatureFieldName);

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = (Date)dateFormat.parse(signingTime);

		calendar.setTime(date);

		PdfSignature pdfSignature  = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("ETSI.CAdES.detached"));
		pdfSignature.setName(signerName);
		pdfSignature.setDate(new PdfDate(calendar));

		if(fieldLock != null) {
			appearance.setFieldLockDict(fieldLock );
		}

		appearance.setCryptoDictionary(pdfSignature);

		appearance.setLayer2Text(signerText + "\n" + signingTime);
		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		appearance.setLayer2Font(new Font(baseFont));

		if(logoB64 != null && logoB64 != "") {
			byte[] inputLogo = Base64.decode(logoB64);
			appearance.setImage(Image.getInstance(inputLogo));
			appearance.setImageScale(-1);
			appearance.setRenderingMode(RenderingMode.DESCRIPTION);
		}
		else {
			appearance.setRenderingMode(RenderingMode.DESCRIPTION);
		}

		HashMap<PdfName, Integer> exclusionSizes = new HashMap<PdfName, Integer>(){
			private static final long serialVersionUID = 1L;
		};

		exclusionSizes.put(PdfName.CONTENTS, CONTENTS_SIZE_ENTRY * 2 + 2);
		appearance.preClose(exclusionSizes);

		InputStream rangeStream = appearance.getRangeStream();

		byte[] rangeBytes = Streams.readAll(rangeStream);
		byte[] hashToBeSigned = IHash.calculateHash(rangeBytes, "SHA-256");

		PdfDictionary contentsDictionary = new PdfDictionary();
		byte[] contentsEntryBytes = new byte[CONTENTS_SIZE_ENTRY];
		contentsDictionary.put(PdfName.CONTENTS, new PdfString(contentsEntryBytes).setHexWriting(true));

		appearance.close(contentsDictionary);

		InserBlankSignatureResponse response = new InserBlankSignatureResponse();
		response.pdfBlankSigned = new String(Base64.encode(output.toByteArray()), "UTF-8");
		response.hashToSignB64 = new String(Base64.encode(hashToBeSigned), "UTF-8");
		return response;
	}

	public static InserBlankSignatureResponse insertBlankSignature(byte[] pdfBytes, SignatureFieldModel signatureField,
			String signerName, String signerText, String signingTime, String logoB64) throws Exception {

		ByteArrayInputStream input = new ByteArrayInputStream(pdfBytes);

		PdfReader reader = new PdfReader(input);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper	stamper = PdfStamper.createSignature(reader, output, '\0', null, true);

		stamper.getWriter().setPdfVersion(PdfWriter.PDF_VERSION_1_7);

		PdfDictionary cadesExtensionDictionary = new PdfDictionary();
		cadesExtensionDictionary.put(PdfName.BASEVERSION, new PdfName("1.7"));
		cadesExtensionDictionary.put(PdfName.EXTENSIONLEVEL, new PdfNumber(2));

		PdfDictionary esicExtensionDictionary = new PdfDictionary();
		esicExtensionDictionary.put(new PdfName("ESIC"), cadesExtensionDictionary);
		reader.getCatalog().put(new PdfName("Extensions"), esicExtensionDictionary);

		PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
		Rectangle signaturePosition = new Rectangle(signatureField.position.x, signatureField.position.y, signatureField.position.x + signatureField.position.width, signatureField.position.y + signatureField.position.height);
		appearance.setVisibleSignature(signaturePosition, signatureField.position.page, signatureField.name);

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-3"));
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = (Date)dateFormat.parse(signingTime);

		calendar.setTime(date);

		PdfSignature pdfSignature  = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("ETSI.CAdES.detached"));
		pdfSignature.setName(signerName);
		pdfSignature.setDate(new PdfDate(calendar));

		appearance.setCryptoDictionary(pdfSignature);

		appearance.setLayer2Text(signerText + "\n" + signingTime);
		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		appearance.setLayer2Font(new Font(baseFont));

		if(logoB64 != null && logoB64 != "") {
			byte[] inputLogo = Base64.decode(logoB64);
			appearance.setImage(Image.getInstance(inputLogo));
			appearance.setImageScale(-1);
			appearance.setRenderingMode(RenderingMode.DESCRIPTION);
		}
		else {
			appearance.setRenderingMode(RenderingMode.DESCRIPTION);
		}

		HashMap<PdfName, Integer> exclusionSizes = new HashMap<PdfName, Integer>(){
			private static final long serialVersionUID = 1L;
		};

		exclusionSizes.put(PdfName.CONTENTS, CONTENTS_SIZE_ENTRY * 2 + 2);
		appearance.preClose(exclusionSizes);

		InputStream rangeStream = appearance.getRangeStream();

		byte[] rangeBytes = Streams.readAll(rangeStream);
		byte[] hashToBeSigned = IHash.calculateHash(rangeBytes, "SHA-256");

		PdfDictionary contentsDictionary = new PdfDictionary();
		byte[] contentsEntryBytes = new byte[CONTENTS_SIZE_ENTRY];
		contentsDictionary.put(PdfName.CONTENTS, new PdfString(contentsEntryBytes).setHexWriting(true));

		appearance.close(contentsDictionary);

		InserBlankSignatureResponse response = new InserBlankSignatureResponse();
		response.pdfBlankSigned = new String(Base64.encode(output.toByteArray()), "UTF-8");
		response.hashToSignB64 = new String(Base64.encode(hashToBeSigned), "UTF-8");
		return response;
	}
	
	public static ReplaceContentsEntryResponse replaceContentsEntry(String fakePdfSignatureB64, String signatureFieldName, String p7sB64) throws Exception {
		byte[] fakePdfSignature = Base64.decode(fakePdfSignatureB64);
		ByteArrayInputStream input = new ByteArrayInputStream(fakePdfSignature);

		PdfReader reader = new PdfReader(input);
		AcroFields fields = reader.getAcroFields();
		reader.close();

		if (!fields.doesSignatureFieldExist(signatureFieldName)) {
			throw new Exception("Não foi possível localizar um campo de assinatura com o nome " + signatureFieldName + ".");
		}

		PdfDictionary sig = fields.getSignatureDictionary(signatureFieldName);

		byte[] p7sBytes = Base64.decode(p7sB64);
		byte[] p7sCleaned = P7sCleaner.cleanSignatureCertificates(p7sBytes);

		PdfString contentsEntry = sig.getAsString(PdfName.CONTENTS);
		byte[] contents = contentsEntry.getOriginalBytes();

		String contentsString = new String(Hex.encode(contents));
		contents = null;

		String newSigHex = new String(Hex.encode(p7sCleaned));
		int bytesToCompleteNewContents = contentsString.length() - newSigHex.length();
		for (int i = 0; i < bytesToCompleteNewContents; i++) {
			newSigHex += "0";
		}

		newSigHex = "<" + newSigHex + ">";

		long[] byteRange = sig.getAsArray(PdfName.BYTERANGE).asLongArray();

		byte[] beforeSigBlock = Arrays.copyOfRange(fakePdfSignature, 0, (int)byteRange[1]);
		byte[] afterSigBlock = Arrays.copyOfRange(fakePdfSignature, (int)byteRange[2], (int)byteRange[2] + (int)byteRange[3]);
		byte[] newSigBlock = newSigHex.getBytes();
		newSigHex = null;

		byte[] newFileBytes = new byte[beforeSigBlock.length + newSigBlock.length + afterSigBlock.length ];
		System.arraycopy(beforeSigBlock, 0, newFileBytes, 0, beforeSigBlock.length);
		System.arraycopy(newSigBlock, 0, newFileBytes, beforeSigBlock.length, newSigBlock.length);
		System.arraycopy(afterSigBlock, 0, newFileBytes, beforeSigBlock.length + newSigBlock.length, afterSigBlock.length);

		beforeSigBlock = null;
		newSigBlock = null;
		afterSigBlock = null;

		ReplaceContentsEntryResponse response = new ReplaceContentsEntryResponse();
		response.pdfB64 = new String(Base64.encode(newFileBytes), "UTF-8");

		return response;
	}

	public static CreateFormResponse createForm(String pdfB64, FormFieldModel[] fields, SignatureFieldModel[] signatureFields) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(pdfB64));

		PdfReader reader = new PdfReader(input);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, output);

		if(fields != null) {
			for(int i = 0 ; i < fields.length ; i++) {
				FormFieldModel currentFieldModel = fields[i];
				Rectangle box = new Rectangle(currentFieldModel.position.x, currentFieldModel.position.y, currentFieldModel.position.x + currentFieldModel.position.width, currentFieldModel.position.y + currentFieldModel.position.height);
				TextField textField = new TextField(stamper.getWriter(), box, currentFieldModel.name);
				textField.setVisibility(TextField.VISIBLE);
				if(currentFieldModel.value != null){
					textField.setText(currentFieldModel.value);
				}
				PdfFormField formField = textField.getTextField();
				stamper.addAnnotation(formField, currentFieldModel.position.page);
			}
		}

		if(signatureFields != null) {

			for(int i = 0 ; i < signatureFields.length ; i++) {
				SignatureFieldModel currentFieldModel = signatureFields[i];

				PdfFormField sigField = PdfFormField.createSignature(stamper.getWriter());

				Rectangle box = new Rectangle(currentFieldModel.position.x, currentFieldModel.position.y, currentFieldModel.position.x + currentFieldModel.position.width, currentFieldModel.position.y + currentFieldModel.position.height);
				sigField.setWidget(box, PdfAnnotation.HIGHLIGHT_INVERT);
				sigField.setFieldName(currentFieldModel.name);
				sigField.setFlags(PdfAnnotation.FLAGS_PRINT);

				sigField.setMKBorderColor(BaseColor.BLACK);
				sigField.setMKBackgroundColor(BaseColor.WHITE);
				PdfAppearance appearance = PdfAppearance.createAppearance(stamper.getWriter(), currentFieldModel.position.width, currentFieldModel.position.height);
				appearance.rectangle(0.5f, 0.5f,  (float)currentFieldModel.position.width - 0.5f, (float)currentFieldModel.position.height - 0.5f);
				appearance.stroke();
				sigField.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, appearance);
				stamper.addAnnotation(sigField, currentFieldModel.position.page);
			}
		}

		stamper.close();

		CreateFormResponse response = new CreateFormResponse();
		response.pdfB64 = new String(Base64.encode(output.toByteArray()), "UTF-8");
		return response;
	}
}

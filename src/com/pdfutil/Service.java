package com.pdfutil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bouncycastle.util.encoders.Base64;

import com.pdfutil.pdf.PdfManager; 

@Path("/")
public class Service {  

	@POST
	@Path("/createForm") 
	@Consumes("application/json")
	@Produces("application/json")
	public Response createForm(CreateFormRequest request) {
		
		if(request == null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorModel("O request não pode ser nulo.")).build();
		}
		
		ErrorModel requestValidationError = request.validate();
		if(requestValidationError != null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(requestValidationError).build();
		}
		
		try {
			CreateFormResponse response = PdfManager.createForm(request.pdfB64, request.fields, request.signatureFields);
			return Response.ok(response).build();
		}
		catch (Exception e) {
			return Response.serverError().entity(new ErrorModel("Erro ao criar o formulário", e)).build();
		}
	}
	
	@POST
	@Path("/insertBlankSignatureForm") 
	@Consumes("application/json")
	@Produces("application/json")
	public Response insertBlankSignatureForm(InserBlankSignatureFormRequest request){ 
		
		if(request == null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorModel("O request não pode ser nulo.")).build();
		}

		ErrorModel requestValidationError = request.validate();
		if(requestValidationError != null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(requestValidationError).build();
		}

		try {
			byte[] pdfFilledAndBlocked = Base64.decode(request.pdfB64);
			
			if(request.fieldsToBlock != null && request.fieldsToBlock.length > 0) {
				pdfFilledAndBlocked = PdfManager.fillForm(pdfFilledAndBlocked, request.fieldsToBlock);
			}
			
			InserBlankSignatureResponse response = PdfManager.insertBlankSignature(
					pdfFilledAndBlocked, 
					request.signatureField.name, 
					request.signerInfo.signerName, 
					request.signerInfo.signerText, 
					request.signerInfo.signingTime, 
					request.signatureField.logoB64,
					request.fieldsToBlock,
					request.blocksWholeForm);
			return Response.ok(response).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ErrorModel("Erro ao criar a assinatura em branco.", e)).build();
		}
	}  

	@POST
	@Path("/insertBlankSignatureMeta") 
	@Consumes("application/json")
	@Produces("application/json")
	public Response insertBlankSignatureMeta(InserBlankSignatureMetaRequest request){ 
		
		if(request == null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorModel("O request não pode ser nulo.")).build();
		}

		ErrorModel requestValidationError = request.validate();
		if(requestValidationError != null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(requestValidationError).build();
		}

		try {
			byte[] pdfFilled = Base64.decode(request.pdfB64);
			
			pdfFilled = PdfManager.copy(pdfFilled);
			
			if(request.metadata != null && request.metadata.length > 0) {
				pdfFilled = PdfManager.insertHeader(pdfFilled, request.metadata, request.createXmp);
			}
			
			InserBlankSignatureResponse response = PdfManager.insertBlankSignature(
					pdfFilled, 
					request.signatureField, 
					request.signerInfo.signerName, 
					request.signerInfo.signerText, 
					request.signerInfo.signingTime, 
					request.signatureField.logoB64);
			return Response.ok(response).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ErrorModel("Erro ao criar a assinatura em branco.", e)).build();
		}
	}  

	
	@POST
	@Path("/replaceContentsEntry") 
	@Consumes("application/json")
	@Produces("application/json")
	public Response replaceContentsEntry(ReplaceContentsEntryRequest request){ 

		if(request == null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorModel("O request não pode ser nulo.")).build();
		}

		ErrorModel requestValidationError = request.validate();
		if(requestValidationError != null) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(requestValidationError).build();
		}

		try {
			ReplaceContentsEntryResponse response = PdfManager.replaceContentsEntry(
					request.blankSignedPdfB64, 
					request.signatureFieldName, 
					request.p7sB64);
			return Response.ok(response).build();
		} catch (Exception e) {
			return Response.serverError().entity(new ErrorModel("Erro ao reconstruir a assinatura.", e)).build();
		}
	}  

}


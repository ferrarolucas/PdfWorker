package com.pdfutil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

public class ErrorModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String Message;
	
	public String ExceptionMessage;
	
	public String Trace;
	
	public ErrorModel(String message) {
		this.Message = message;
		this.ExceptionMessage = "";
		this.Trace = "";
	}
	
	public ErrorModel(String message, Exception e) {
		this.Message = message;
		this.ExceptionMessage = e.getMessage();
		ByteArrayOutputStream traceStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(traceStream);
		e.printStackTrace(ps);
		
		this.Trace = new String(traceStream.toByteArray());
	}
	
}

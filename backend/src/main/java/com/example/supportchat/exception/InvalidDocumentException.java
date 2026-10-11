package com.example.supportchat.exception;

/** The upload is not acceptable (missing, empty or not a PDF). */
public class InvalidDocumentException extends RuntimeException {

	public InvalidDocumentException(String message) {
		super(message);
	}

}

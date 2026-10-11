package com.example.supportchat.exception;

/** A valid upload that could not be turned into indexed content (unreadable or text-less PDF). */
public class DocumentProcessingException extends RuntimeException {

	public DocumentProcessingException(String message) {
		super(message);
	}

	public DocumentProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

}

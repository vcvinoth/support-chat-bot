package com.example.supportchat.exception;

/** The call to Claude failed or returned nothing usable. */
public class AiServiceException extends RuntimeException {

	public AiServiceException(String message) {
		super(message);
	}

	public AiServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}

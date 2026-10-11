package com.example.supportchat.service;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

/** In-memory record of an uploaded document and its indexing result. */
public record DocumentInfo(
		String id,
		String fileName,
		Status status,
		int pages,
		int chunks,
		@Nullable String error,
		Instant uploadedAt) {

	public enum Status {
		INDEXED, FAILED
	}

}

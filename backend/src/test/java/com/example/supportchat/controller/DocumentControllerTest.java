package com.example.supportchat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.supportchat.exception.DocumentProcessingException;
import com.example.supportchat.exception.InvalidDocumentException;
import com.example.supportchat.service.DocumentInfo;
import com.example.supportchat.service.DocumentIngestionService;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DocumentIngestionService ingestionService;

	private final DocumentInfo indexed = new DocumentInfo("id-1", "guide.pdf", DocumentInfo.Status.INDEXED, 3, 7, null,
			Instant.parse("2026-10-11T00:00:00Z"));

	@Test
	void uploadReturnsCreatedDocument() throws Exception {
		when(ingestionService.ingest(eq("guide.pdf"), any())).thenReturn(indexed);

		mockMvc.perform(multipart("/api/documents").file(pdf("guide.pdf")))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value("id-1"))
			.andExpect(jsonPath("$.status").value("INDEXED"))
			.andExpect(jsonPath("$.chunks").value(7));
	}

	@Test
	void invalidUploadReturnsBadRequest() throws Exception {
		when(ingestionService.ingest(any(), any())).thenThrow(new InvalidDocumentException("Only PDF files are supported."));

		mockMvc.perform(multipart("/api/documents").file(pdf("notes.txt")))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.detail").value("Only PDF files are supported."));
	}

	@Test
	void unprocessablePdfReturns422() throws Exception {
		when(ingestionService.ingest(any(), any())).thenThrow(new DocumentProcessingException("no text"));

		mockMvc.perform(multipart("/api/documents").file(pdf("scan.pdf")))
			.andExpect(status().isUnprocessableContent())
			.andExpect(jsonPath("$.detail").value("no text"));
	}

	@Test
	void listReturnsDocuments() throws Exception {
		when(ingestionService.list()).thenReturn(List.of(indexed));

		mockMvc.perform(get("/api/documents"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].fileName").value("guide.pdf"));
	}

	private static MockMultipartFile pdf(String name) {
		return new MockMultipartFile("file", name, "application/pdf", "%PDF-1.7".getBytes());
	}

}

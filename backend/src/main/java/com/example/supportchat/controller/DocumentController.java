package com.example.supportchat.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.supportchat.exception.InvalidDocumentException;
import com.example.supportchat.service.DocumentInfo;
import com.example.supportchat.service.DocumentIngestionService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

	private final DocumentIngestionService ingestionService;

	public DocumentController(DocumentIngestionService ingestionService) {
		this.ingestionService = ingestionService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public DocumentInfo upload(@RequestParam("file") MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		if (fileName == null || fileName.isBlank()) {
			throw new InvalidDocumentException("The uploaded file has no name.");
		}
		return ingestionService.ingest(fileName, file.getBytes());
	}

	@GetMapping
	public List<DocumentInfo> list() {
		return ingestionService.list();
	}

}

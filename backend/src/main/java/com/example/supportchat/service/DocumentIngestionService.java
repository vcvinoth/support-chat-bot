package com.example.supportchat.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.example.supportchat.exception.DocumentProcessingException;
import com.example.supportchat.exception.InvalidDocumentException;
import com.example.supportchat.service.PdfTextExtractor.PageText;

/** Turns an uploaded PDF into page-tagged chunks and stores them in the vector store. */
@Service
public class DocumentIngestionService {

	public static final String META_DOCUMENT_ID = "documentId";
	public static final String META_FILE_NAME = "fileName";
	public static final String META_PAGE = "page";

	private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

	private static final byte[] PDF_MAGIC = { '%', 'P', 'D', 'F', '-' };

	private final PdfTextExtractor extractor;

	private final TokenTextSplitter splitter;

	private final VectorStore vectorStore;

	private final Map<String, DocumentInfo> documents = Collections.synchronizedMap(new LinkedHashMap<>());

	/** Vector store chunk IDs per document ID; guarded by the {@code documents} lock. */
	private final Map<String, List<String>> chunkIds = new HashMap<>();

	public DocumentIngestionService(PdfTextExtractor extractor, TokenTextSplitter splitter, VectorStore vectorStore) {
		this.extractor = extractor;
		this.splitter = splitter;
		this.vectorStore = vectorStore;
	}

	/**
	 * Indexes a PDF. If a document with the same file name was uploaded before, the new copy replaces it once
	 * indexing succeeds; if indexing fails, an earlier indexed copy is kept.
	 */
	public DocumentInfo ingest(String fileName, byte[] content) {
		validate(fileName, content);
		String id = UUID.randomUUID().toString();
		Instant uploadedAt = Instant.now();
		try {
			List<PageText> pages = extractor.extract(content);
			List<Document> chunks = splitter.split(toDocuments(id, fileName, pages));
			vectorStore.add(chunks);
			DocumentInfo info = new DocumentInfo(id, fileName, DocumentInfo.Status.INDEXED, pages.size(),
					chunks.size(), null, uploadedAt);
			synchronized (documents) {
				int replaced = removeEarlier(fileName, false);
				documents.put(id, info);
				chunkIds.put(id, chunks.stream().map(Document::getId).toList());
				log.info("Indexed '{}' ({} pages with text, {} chunks{})", fileName, pages.size(), chunks.size(),
						replaced > 0 ? ", replaced earlier copy" : "");
			}
			return info;
		}
		catch (DocumentProcessingException ex) {
			synchronized (documents) {
				removeEarlier(fileName, true);
				documents.put(id, new DocumentInfo(id, fileName, DocumentInfo.Status.FAILED, 0, 0, ex.getMessage(),
						uploadedAt));
			}
			log.warn("Failed to index '{}': {}", fileName, ex.getMessage());
			throw ex;
		}
	}

	public List<DocumentInfo> list() {
		synchronized (documents) {
			return new ArrayList<>(documents.values());
		}
	}

	/**
	 * Removes earlier entries with the given file name, and their chunks from the vector store. Caller must hold
	 * the {@code documents} lock.
	 * @param failedOnly only remove entries whose indexing failed
	 * @return number of entries removed
	 */
	private int removeEarlier(String fileName, boolean failedOnly) {
		List<DocumentInfo> earlier = documents.values()
			.stream()
			.filter(doc -> doc.fileName().equals(fileName))
			.filter(doc -> !failedOnly || doc.status() == DocumentInfo.Status.FAILED)
			.toList();
		for (DocumentInfo doc : earlier) {
			documents.remove(doc.id());
			List<String> ids = chunkIds.remove(doc.id());
			if (ids != null && !ids.isEmpty()) {
				vectorStore.delete(ids);
			}
		}
		return earlier.size();
	}

	private static List<Document> toDocuments(String id, String fileName, List<PageText> pages) {
		return pages.stream()
			.map(page -> new Document(page.text(),
					Map.of(META_DOCUMENT_ID, id, META_FILE_NAME, fileName, META_PAGE, page.page())))
			.toList();
	}

	private static void validate(String fileName, byte[] content) {
		if (content.length == 0) {
			throw new InvalidDocumentException("The uploaded file is empty.");
		}
		if (!fileName.toLowerCase().endsWith(".pdf") || !startsWithPdfMagic(content)) {
			throw new InvalidDocumentException("Only PDF files are supported.");
		}
	}

	private static boolean startsWithPdfMagic(byte[] content) {
		if (content.length < PDF_MAGIC.length) {
			return false;
		}
		for (int i = 0; i < PDF_MAGIC.length; i++) {
			if (content[i] != PDF_MAGIC[i]) {
				return false;
			}
		}
		return true;
	}

}

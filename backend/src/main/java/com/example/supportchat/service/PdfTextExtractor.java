package com.example.supportchat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.example.supportchat.exception.DocumentProcessingException;

/** Extracts plain text from a PDF, one entry per non-blank page. */
@Component
public class PdfTextExtractor {

	public record PageText(int page, String text) {
	}

	public List<PageText> extract(byte[] pdfBytes) {
		try (PDDocument document = Loader.loadPDF(pdfBytes)) {
			PDFTextStripper stripper = new PDFTextStripper();
			List<PageText> pages = new ArrayList<>();
			for (int page = 1; page <= document.getNumberOfPages(); page++) {
				stripper.setStartPage(page);
				stripper.setEndPage(page);
				String text = stripper.getText(document).strip();
				if (!text.isEmpty()) {
					pages.add(new PageText(page, text));
				}
			}
			if (pages.isEmpty()) {
				throw new DocumentProcessingException(
						"The PDF contains no extractable text (it may be a scanned image).");
			}
			return pages;
		}
		catch (IOException ex) {
			throw new DocumentProcessingException("The PDF could not be read: " + ex.getMessage(), ex);
		}
	}

}

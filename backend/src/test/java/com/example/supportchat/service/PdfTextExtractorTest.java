package com.example.supportchat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import com.example.supportchat.TestPdfs;
import com.example.supportchat.exception.DocumentProcessingException;
import com.example.supportchat.service.PdfTextExtractor.PageText;

class PdfTextExtractorTest {

	private final PdfTextExtractor extractor = new PdfTextExtractor();

	@Test
	void extractsTextPerPageFromSamplePdf() throws IOException {
		List<PageText> pages = extractor.extract(TestPdfs.sample());

		assertThat(pages).isNotEmpty();
		assertThat(pages).allSatisfy(page -> {
			assertThat(page.page()).isPositive();
			assertThat(page.text()).isNotBlank();
		});
		assertThat(pages).extracting(PageText::page).isSorted();
	}

	@Test
	void rejectsPdfWithoutText() throws IOException {
		byte[] blankPdf;
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			document.addPage(new PDPage());
			document.save(out);
			blankPdf = out.toByteArray();
		}

		assertThatThrownBy(() -> extractor.extract(blankPdf)).isInstanceOf(DocumentProcessingException.class)
			.hasMessageContaining("no extractable text");
	}

	@Test
	void rejectsCorruptPdf() {
		assertThatThrownBy(() -> extractor.extract("%PDF-garbage".getBytes()))
			.isInstanceOf(DocumentProcessingException.class);
	}

}

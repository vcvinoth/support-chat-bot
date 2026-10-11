package com.example.supportchat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/** Builds a small text-based sample PDF in memory, so tests need no external documents. */
public final class TestPdfs {

	public static final String SAMPLE_NAME = "sample-guide.pdf";

	private static final List<List<String>> PAGES = List.of(
			List.of("Portal Quick Start Guide", "This guide explains how to sign in to the support portal.",
					"Open the portal address in a supported browser such as Chrome or Edge."),
			List.of("Signing in", "Enter your user ID and password, then select Sign In.",
					"If two-factor authentication is enabled, enter the code sent to your phone.",
					"After three failed attempts the account is locked for fifteen minutes."),
			List.of("Resetting your password", "Select Forgot Password on the sign-in page.",
					"Follow the link in the email to choose a new password.",
					"Contact the helpdesk if you do not receive the email within ten minutes."));

	private TestPdfs() {
	}

	public static byte[] sample() throws IOException {
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
			for (List<String> lines : PAGES) {
				PDPage page = new PDPage();
				document.addPage(page);
				try (PDPageContentStream content = new PDPageContentStream(document, page)) {
					content.beginText();
					content.setFont(font, 12);
					content.setLeading(16);
					content.newLineAtOffset(72, 720);
					for (String line : lines) {
						content.showText(line);
						content.newLine();
					}
					content.endText();
				}
			}
			document.save(out);
			return out.toByteArray();
		}
	}

}

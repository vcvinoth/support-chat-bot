package com.example.supportchat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import com.example.supportchat.TestPdfs;
import com.example.supportchat.exception.DocumentProcessingException;
import com.example.supportchat.exception.InvalidDocumentException;

class DocumentIngestionServiceTest {

	private final VectorStore vectorStore = mock(VectorStore.class);

	private final PdfTextExtractor extractor = new PdfTextExtractor();

	private final DocumentIngestionService service = new DocumentIngestionService(extractor,
			TokenTextSplitter.builder().withChunkSize(200).withMinChunkSizeChars(100).build(), vectorStore);

	@Test
	@SuppressWarnings("unchecked")
	void indexesSamplePdfWithPageMetadata() throws IOException {
		DocumentInfo info = service.ingest(TestPdfs.SAMPLE_NAME, TestPdfs.sample());

		ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
		verify(vectorStore).add(captor.capture());
		List<Document> chunks = captor.getValue();

		assertThat(info.status()).isEqualTo(DocumentInfo.Status.INDEXED);
		assertThat(info.pages()).isPositive();
		assertThat(info.chunks()).isEqualTo(chunks.size()).isGreaterThanOrEqualTo(info.pages());
		assertThat(chunks).allSatisfy(chunk -> {
			assertThat(chunk.getText()).isNotBlank();
			assertThat(chunk.getMetadata()).containsEntry(DocumentIngestionService.META_DOCUMENT_ID, info.id())
				.containsEntry(DocumentIngestionService.META_FILE_NAME, TestPdfs.SAMPLE_NAME)
				.containsKey(DocumentIngestionService.META_PAGE);
		});
		assertThat(service.list()).containsExactly(info);
	}

	@Test
	void rejectsNonPdf() {
		assertThatThrownBy(() -> service.ingest("notes.txt", "hello".getBytes()))
			.isInstanceOf(InvalidDocumentException.class);
		assertThatThrownBy(() -> service.ingest("fake.pdf", "hello".getBytes()))
			.isInstanceOf(InvalidDocumentException.class);
		verify(vectorStore, never()).add(any());
		assertThat(service.list()).isEmpty();
	}

	@Test
	void rejectsEmptyFile() {
		assertThatThrownBy(() -> service.ingest("empty.pdf", new byte[0])).isInstanceOf(InvalidDocumentException.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	void reuploadWithSameNameReplacesEarlierCopyAndItsChunks() throws IOException {
		DocumentInfo first = service.ingest(TestPdfs.SAMPLE_NAME, TestPdfs.sample());
		DocumentInfo second = service.ingest(TestPdfs.SAMPLE_NAME, TestPdfs.sample());

		ArgumentCaptor<List<Document>> added = ArgumentCaptor.forClass(List.class);
		verify(vectorStore, times(2)).add(added.capture());
		List<String> firstChunkIds = added.getAllValues().get(0).stream().map(Document::getId).toList();

		verify(vectorStore).delete(firstChunkIds);
		assertThat(service.list()).containsExactly(second);
		assertThat(second.id()).isNotEqualTo(first.id());
	}

	@Test
	void differentFileNamesAreKept() throws IOException {
		service.ingest("a.pdf", TestPdfs.sample());
		service.ingest("b.pdf", TestPdfs.sample());

		assertThat(service.list()).extracting(DocumentInfo::fileName).containsExactly("a.pdf", "b.pdf");
		verify(vectorStore, never()).delete(anyList());
	}

	@Test
	void failedReuploadKeepsEarlierIndexedCopy() {
		PdfTextExtractor extractorMock = mock(PdfTextExtractor.class);
		when(extractorMock.extract(any())).thenReturn(List.of(new PdfTextExtractor.PageText(1, "Some text")))
			.thenThrow(new DocumentProcessingException("no text"));
		DocumentIngestionService svc = new DocumentIngestionService(extractorMock, new TokenTextSplitter(),
				vectorStore);

		DocumentInfo indexed = svc.ingest("guide.pdf", "%PDF-1.7".getBytes());
		assertThatThrownBy(() -> svc.ingest("guide.pdf", "%PDF-1.7".getBytes()))
			.isInstanceOf(DocumentProcessingException.class);

		assertThat(svc.list()).hasSize(2).first().isEqualTo(indexed);
		assertThat(svc.list().get(1).status()).isEqualTo(DocumentInfo.Status.FAILED);
		verify(vectorStore, never()).delete(anyList());
	}

	@Test
	void successfulReuploadReplacesEarlierFailedEntry() {
		PdfTextExtractor extractorMock = mock(PdfTextExtractor.class);
		when(extractorMock.extract(any())).thenThrow(new DocumentProcessingException("no text"))
			.thenReturn(List.of(new PdfTextExtractor.PageText(1, "Some text")));
		DocumentIngestionService svc = new DocumentIngestionService(extractorMock, new TokenTextSplitter(),
				vectorStore);

		assertThatThrownBy(() -> svc.ingest("guide.pdf", "%PDF-1.7".getBytes()))
			.isInstanceOf(DocumentProcessingException.class);
		DocumentInfo indexed = svc.ingest("guide.pdf", "%PDF-1.7".getBytes());

		assertThat(svc.list()).containsExactly(indexed);
		verify(vectorStore, never()).delete(anyList());
	}

	@Test
	void recordsFailedDocumentWhenProcessingFails() {
		PdfTextExtractor failingExtractor = mock(PdfTextExtractor.class);
		when(failingExtractor.extract(any())).thenThrow(new DocumentProcessingException("no text"));
		DocumentIngestionService failingService = new DocumentIngestionService(failingExtractor,
				new TokenTextSplitter(), vectorStore);

		assertThatThrownBy(() -> failingService.ingest("scan.pdf", "%PDF-1.7".getBytes()))
			.isInstanceOf(DocumentProcessingException.class);

		assertThat(failingService.list()).singleElement().satisfies(info -> {
			assertThat(info.status()).isEqualTo(DocumentInfo.Status.FAILED);
			assertThat(info.error()).isEqualTo("no text");
		});
		verify(vectorStore, never()).add(any());
	}

}

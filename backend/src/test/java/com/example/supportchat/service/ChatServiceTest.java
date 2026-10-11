package com.example.supportchat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import com.example.supportchat.config.RagProperties;
import com.example.supportchat.exception.AiServiceException;
import com.example.supportchat.service.ChatService.Source;

class ChatServiceTest {

	private final VectorStore vectorStore = mock(VectorStore.class);

	private final ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);

	private final ChatService service = new ChatService(vectorStore, chatClient, new RagProperties(200, 100, 5, 0.35));

	@Test
	void returnsNotFoundWithoutCallingClaudeWhenNothingRelevant() {
		when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

		ChatService.Answer answer = service.answer("How do I reset my password?");

		assertThat(answer.answer()).isEqualTo(ChatService.NOT_FOUND_ANSWER);
		assertThat(answer.sources()).isEmpty();
		verifyNoInteractions(chatClient);
	}

	@Test
	void answersFromRetrievedChunksWithDeduplicatedSources() {
		when(vectorStore.similaritySearch(any(SearchRequest.class)))
			.thenReturn(List.of(chunk("guide.pdf", 3, "Click Forgot Password."), chunk("guide.pdf", 3, "Check email."),
					chunk("guide.pdf", 5, "Contact the helpdesk.")));
		when(chatClient.prompt().system(anyString()).user(anyString()).call().content()).thenReturn(" Use Forgot Password. ");

		ChatService.Answer answer = service.answer("How do I reset my password?");

		assertThat(answer.answer()).isEqualTo("Use Forgot Password.");
		assertThat(answer.sources()).containsExactly(new Source("guide.pdf", 3), new Source("guide.pdf", 5));

		ArgumentCaptor<SearchRequest> search = ArgumentCaptor.forClass(SearchRequest.class);
		verify(vectorStore).similaritySearch(search.capture());
		assertThat(search.getValue().getTopK()).isEqualTo(5);
		assertThat(search.getValue().getSimilarityThreshold()).isEqualTo(0.35);
	}

	@Test
	void userMessageContainsExcerptsWithSourceAndQuestion() {
		String message = ChatService.userMessage("How do I log in?", List.of(chunk("guide.pdf", 2, "Open the portal.")));

		assertThat(message).contains("<documents>")
			.contains("file=\"guide.pdf\" page=\"2\"")
			.contains("Open the portal.")
			.endsWith("Question: How do I log in?");
	}

	@Test
	void wrapsClaudeFailure() {
		when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(chunk("guide.pdf", 1, "text")));
		when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
			.thenThrow(new IllegalStateException("401 invalid api key"));

		assertThatThrownBy(() -> service.answer("question")).isInstanceOf(AiServiceException.class)
			.hasMessageContaining("unavailable");
	}

	private static Document chunk(String fileName, int page, String text) {
		return new Document(text, Map.of(DocumentIngestionService.META_FILE_NAME, fileName,
				DocumentIngestionService.META_PAGE, page));
	}

}

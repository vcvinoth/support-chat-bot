package com.example.supportchat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

	/** In-memory vector store; contents are lost when the application stops. */
	@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

	@Bean
	public ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}

	@Bean
	public TokenTextSplitter textSplitter(RagProperties properties) {
		return TokenTextSplitter.builder()
			.withChunkSize(properties.chunkSize())
			.withMinChunkSizeChars(properties.minChunkSizeChars())
			.build();
	}

}

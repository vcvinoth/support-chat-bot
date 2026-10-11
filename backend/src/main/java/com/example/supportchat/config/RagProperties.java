package com.example.supportchat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Tunable settings for document chunking and retrieval.
 *
 * @param chunkSize target chunk size in tokens
 * @param minChunkSizeChars minimum characters before the splitter looks for a sentence break
 * @param topK maximum number of chunks sent to Claude per question
 * @param similarityThreshold minimum cosine similarity (0-1) for a chunk to count as relevant
 */
@ConfigurationProperties("app.rag")
public record RagProperties(
		@DefaultValue("200") int chunkSize,
		@DefaultValue("100") int minChunkSizeChars,
		@DefaultValue("5") int topK,
		@DefaultValue("0.35") double similarityThreshold) {
}

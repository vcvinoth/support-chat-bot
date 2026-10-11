package com.example.supportchat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// The real ONNX model is downloaded on startup; replace it so tests run offline.
@SpringBootTest(properties = "spring.ai.model.embedding=none")
class SupportChatBotBackendApplicationTests {

	@MockitoBean
	private EmbeddingModel embeddingModel;

	@Test
	void contextLoads() {
	}

}

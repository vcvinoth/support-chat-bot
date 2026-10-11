package com.example.supportchat.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.supportchat.exception.AiServiceException;
import com.example.supportchat.service.ChatService;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ChatService chatService;

	@Test
	void returnsAnswerWithSources() throws Exception {
		when(chatService.answer("How do I log in?")).thenReturn(
				new ChatService.Answer("Open the portal.", List.of(new ChatService.Source("guide.pdf", 2))));

		mockMvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON)
				.content("{\"question\":\"  How do I log in?  \"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.answer").value("Open the portal."))
			.andExpect(jsonPath("$.sources[0].fileName").value("guide.pdf"))
			.andExpect(jsonPath("$.sources[0].page").value(2));
	}

	@Test
	void rejectsBlankQuestion() throws Exception {
		mockMvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"  \"}"))
			.andExpect(status().isBadRequest());
		verifyNoInteractions(chatService);
	}

	@Test
	void aiFailureReturnsBadGateway() throws Exception {
		when(chatService.answer("hi")).thenThrow(new AiServiceException("The AI service is currently unavailable."));

		mockMvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"hi\"}"))
			.andExpect(status().isBadGateway())
			.andExpect(jsonPath("$.detail").value("The AI service is currently unavailable."));
	}

}

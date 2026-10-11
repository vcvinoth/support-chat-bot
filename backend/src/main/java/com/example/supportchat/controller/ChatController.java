package com.example.supportchat.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.supportchat.service.ChatService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	public record ChatRequest(@NotBlank @Size(max = 2000) String question) {
	}

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping
	public ChatService.Answer chat(@Validated @RequestBody ChatRequest request) {
		return chatService.answer(request.question().strip());
	}

}

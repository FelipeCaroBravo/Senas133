package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.MensajeChatRequest;
import com.mediaciondirecta.dto.MensajeChatResponse;
import com.mediaciondirecta.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/emergencias/{id}/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<MensajeChatResponse> listarMensajes(@PathVariable Long id) {
        return chatService.listarMensajes(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeChatResponse enviarMensaje(@PathVariable Long id,
                                             @Valid @RequestBody MensajeChatRequest request) {
        return chatService.enviarMensaje(id, request);
    }
}

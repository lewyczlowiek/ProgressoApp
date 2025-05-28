package ProgressoApp.controllers;

import ProgressoApp.model.ChatMessage;
import ProgressoApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessage send(ChatMessage message, Principal principal) {
        String username = principal.getName(); // Pobierz nazwę użytkownika z Principal
        message.setFrom(username); // Ustaw nazwę użytkownika w obiekcie ChatMessage
        return message;
    }

    @GetMapping("/chat")
    public String chat(Model model, Principal principal) {
        String username = principal.getName(); // Pobierz nazwę użytkownika z Principal
        model.addAttribute("username", username); // Dodaj do modelu
        model.addAttribute("users", userService.findAll()); // Dodaj listę użytkowników
        return "chat";
    }
}
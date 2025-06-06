/*
// ChatController.java
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
        if (principal != null) {
            message.setFrom(principal.getName());
        } else {
            message.setFrom("Anonim");
        }
        return message;
    }

    @GetMapping("/chat")
    public String chat(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        model.addAttribute("username", username);
        model.addAttribute("users", userService.findAll());
        return "chat";
    }
}
*/

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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class ChatController {

    private final List<ChatMessage> messageHistory = new CopyOnWriteArrayList<>();

    @Autowired
    private UserService userService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessage send(ChatMessage message, Principal principal) {
        if (principal != null) {
            message.setFrom(principal.getName());
        } else {
            message.setFrom("Anonim");
        }

        // Dodanie do historii i ograniczenie do 50 ostatnich wiadomości
        messageHistory.add(message);
        if (messageHistory.size() > 50) {
            messageHistory.remove(0);
        }

        return message;
    }

    @GetMapping("/chat")
    public String chat(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        model.addAttribute("username", username);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("messages", messageHistory); // dodaj historię wiadomości
        return "chat";
    }
}

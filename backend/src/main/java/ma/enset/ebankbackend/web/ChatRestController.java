package ma.enset.ebankbackend.web;

import ma.enset.ebankbackend.dtos.ChatRequestDTO;
import ma.enset.ebankbackend.dtos.ChatResponseDTO;
import ma.enset.ebankbackend.services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin("*")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAuthority('USER')")
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La question est obligatoire");
        }
        String answer = chatService.ask(request.question().trim());
        return new ChatResponseDTO(answer);
    }
}

package com.panduranga.Mcp_client.controller;

import com.panduranga.Mcp_client.dto.ChatRequest;
import com.panduranga.Mcp_client.dto.ChatResponse;
import com.panduranga.Mcp_client.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes a single endpoint for the chat interface.
 *
 * <p>{@code POST /api/chat} — accepts a JSON body with a {@code message} field
 * and returns Gemini's response after any required MCP tool invocations.
 *
 * <p>The controller does not contain any business logic; it delegates entirely
 * to {@link ChatService}.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    // Constructor injection — no @Autowired needed in Spring Boot
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Process a user's natural-language filesystem request.
     *
     * <p>Example request body:
     * <pre>{@code
     * {
     *   "message": "List all files on my Desktop"
     * }
     * }</pre>
     *
     * <p>Example response body:
     * <pre>{@code
     * {
     *   "response": "Here are the files on your Desktop: ..."
     * }
     * }</pre>
     *
     * @param request the incoming chat request
     * @return 200 OK with Gemini's response, or an appropriate error status
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: message=[{}]", request.message());

        String responseText = chatService.chat(request.message());

        log.info("Responding with {} characters", responseText != null ? responseText.length() : 0);

        return ResponseEntity.ok(new ChatResponse(responseText));
    }
}

package com.panduranga.Mcp_client.service;

import com.panduranga.Mcp_client.exception.InvalidMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Core service that sends user messages to Google Gemini via the
 * {@link ChatClient}.
 *
 * <p>Gemini decides autonomously whether to call an MCP filesystem tool.
 * Spring AI intercepts the tool-call request, routes it through the MCP
 * client over STDIO to the Filesystem MCP server, and feeds the result
 * back into Gemini for a final natural-language response.
 *
 * <p>No Java {@code File} APIs are used here. All filesystem operations
 * are performed by the external {@code @modelcontextprotocol/server-filesystem}
 * Node.js process.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    // -------------------------------------------------------------------------
    // System prompt
    // -------------------------------------------------------------------------

    /**
     * System prompt that shapes the assistant's behavior.
     * Rules are intentionally strict to ensure MCP tools are used correctly
     * and the user is never misled about what actually happened on the filesystem.
     */
    private static final String SYSTEM_PROMPT = """
            You are a filesystem assistant. You have MCP filesystem tools available.
            Rules:
            - Always use MCP tools for filesystem operations. Never pretend an operation happened.
            - Only access directories allowed by the MCP server.
            - Explain results clearly and concisely.
            - If an operation fails, explain why.
            """;

    // -------------------------------------------------------------------------
    // Constructor injection
    // -------------------------------------------------------------------------

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Sends the user's message to Gemini.
     * Gemini may call one or more MCP filesystem tools before returning a
     * final response. Spring AI handles the tool-call lifecycle automatically.
     *
     * @param userMessage the raw message from the user
     * @return Gemini's final natural-language response
     * @throws InvalidMessageException if the message is null or blank
     */
    public String chat(String userMessage) {
        validateMessage(userMessage);

        log.debug("Sending message to Gemini via ChatClient: [{}]", userMessage);

        try {
            String response = chatClient
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userMessage)
                    .call()
                    .content();

            log.debug("Received response from Gemini (length={})", response != null ? response.length() : 0);
            return response;

        } catch (Exception ex) {
            log.error("Error during ChatClient call for message=[{}]", userMessage, ex);
            throw ex; // Propagate to GlobalExceptionHandler
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new InvalidMessageException("Chat message must not be null or blank.");
        }
    }
}

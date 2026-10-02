package com.panduranga.Mcp_client.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Centralised error handler for all REST endpoints.
 * Returns structured JSON error bodies without exposing stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Handles missing or blank chat messages. */
    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<Map<String, String>> handleInvalidMessage(InvalidMessageException ex) {
        log.warn("Invalid chat request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Catches any Spring AI / Gemini API failure.
     * We log the full exception but return a safe message to the caller.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected error while processing chat request", ex);

        String userMessage = buildUserFriendlyMessage(ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", userMessage));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String buildUserFriendlyMessage(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) {
            return "An unexpected error occurred. Please try again later.";
        }

        // Surface common root causes without leaking stack traces
        if (msg.contains("API key") || msg.contains("UNAUTHENTICATED") || msg.contains("403")) {
            return "Gemini authentication failed. Please verify your GOOGLE_API_KEY environment variable.";
        }
        if (msg.contains("timeout") || msg.contains("Timeout")) {
            return "The MCP server or Gemini request timed out. Please try again.";
        }
        if (msg.contains("MCP") || msg.contains("mcp") || msg.contains("stdio") || msg.contains("process")) {
            return "Failed to communicate with the Filesystem MCP server. "
                    + "Ensure Node.js and npx are installed and the allowed directory in mcp-servers.json is accessible.";
        }
        if (msg.contains("permission") || msg.contains("Permission") || msg.contains("Access")) {
            return "Filesystem permission denied. The MCP server cannot access the requested file or directory.";
        }

        return "An unexpected error occurred. Please check the server logs for details.";
    }
}

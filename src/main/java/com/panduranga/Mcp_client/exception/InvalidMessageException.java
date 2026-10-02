package com.panduranga.Mcp_client.exception;

/**
 * Thrown when the incoming chat message is null or blank.
 */
public class InvalidMessageException extends RuntimeException {

    public InvalidMessageException(String message) {
        super(message);
    }
}

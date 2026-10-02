package com.panduranga.Mcp_client.dto;

/**
 * Outgoing response body for POST /api/chat.
 * Using a record keeps it immutable and removes boilerplate.
 */
public record ChatResponse(String response) {
}

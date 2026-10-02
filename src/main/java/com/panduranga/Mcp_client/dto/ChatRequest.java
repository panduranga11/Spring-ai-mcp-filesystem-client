package com.panduranga.Mcp_client.dto;

/**
 * Incoming request body for POST /api/chat.
 * Using a record keeps it immutable and removes boilerplate.
 */
public record ChatRequest(String message) {
}

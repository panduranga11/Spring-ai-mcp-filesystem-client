package com.panduranga.Mcp_client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the ChatClient bean and wires in the MCP filesystem tools.
 *
 * McpToolCallbackAutoConfiguration (from spring-ai-starter-mcp-client)
 * registers a ToolCallbackProvider bean automatically when
 * spring.ai.mcp.client.toolcallback.enabled=true. We inject that interface
 * here instead of the concrete SyncMcpToolCallbackProvider to avoid
 * Spring Boot 4.x condition-scan classloading failures.
 */
@Configuration
public class ChatClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ChatClientConfig.class);

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider toolCallbackProvider) {
        log.info("Building ChatClient with: {}", toolCallbackProvider.getClass().getSimpleName());
        return builder
                .defaultTools(toolCallbackProvider)
                .build();
    }
}

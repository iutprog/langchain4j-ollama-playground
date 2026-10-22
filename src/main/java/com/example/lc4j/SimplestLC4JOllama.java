package com.example.lc4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllama {
    public static void main(String[] args) {
        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "mistral-nemo"))
                .timeout(Duration.ofSeconds(120))
                .logRequests(true)    // shows the raw /api/chat JSON - compare with OllamaAgentLoop
                .logResponses(true)
                .build();

        System.out.println(cmodel.chat("What are the benefits of Java"));
    }
}

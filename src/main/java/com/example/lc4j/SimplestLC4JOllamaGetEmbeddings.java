package com.example.lc4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 * - no API key: the model runs locally in Podman
 * - baseUrl instead of a vendor endpoint
 * - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaGetEmbeddings {

    public static void main(String[] args) {
        EmbeddingModel eModel = OllamaEmbeddingModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "nomic-embed-text"))
                .timeout(Duration.ofSeconds(600))
                .build();
        Response<Embedding> response = eModel.embed("One fish, two fish, red fish, blue fist");
        Embedding embedding = response.content();

        System.out.println(embedding);

    }
}

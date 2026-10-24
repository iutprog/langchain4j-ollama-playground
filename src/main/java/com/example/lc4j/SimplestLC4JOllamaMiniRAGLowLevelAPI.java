package com.example.lc4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 * - no API key: the model runs locally in Podman
 * - baseUrl instead of a vendor endpoint
 * - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaMiniRAGLowLevelAPI {
    private static final Scanner USER_INPUT = new Scanner(System.in);

    public static void main(String[] args) {
        String cmdLine;
        InMemoryEmbeddingStore<TextSegment> myDB = new InMemoryEmbeddingStore<>();

        EmbeddingModel eModel = createEmbeddingModel();

        loadEmbeddingsFromFile(myDB, eModel, "src/main/java/com/example/hotline/firstlevel");

        ChatModel cModel = createChatModel();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        while (true) {
            if ((cmdLine = getInput("cmd> ")).isEmpty())
                continue;
            UserMessage userMessage = UserMessage.from(cmdLine); // create the initial prompt

            List<String> match = getMatches(myDB, eModel, cmdLine); // Get list of matches from the DB

            for (String m : match) { // Add matches to the memory
                memory.add(UserMessage.from(m));
            }

            memory.add(userMessage); // Finally, add the user's prompt
            var answer = cModel.chat(memory.messages()); // Send everything to the LLM
            var response = answer.aiMessage().text();

            System.out.println(response);

            memory.add(UserMessage.from(response)); // Add response from LLM

        }

    }

    private static String getInput(String input) {
        System.out.print(input);
        return USER_INPUT.nextLine();
    }

    private static ChatModel createChatModel() {
        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();
        return cmodel;
    }

    private static EmbeddingModel createEmbeddingModel() {
        EmbeddingModel eModel = OllamaEmbeddingModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "nomic-embed-text"))
                .timeout(Duration.ofSeconds(600))
                .build();
        return eModel;
    }

    private static List<String> getMatches(InMemoryEmbeddingStore<TextSegment> myDB, EmbeddingModel eModel,
            String cmdLine) {
        // Embed the user's query so it can be compared with the document embeddings in
        // the store.
        var queryEmbedding = eModel.embed(cmdLine).content();

        // Ask the store for the three most similar segments to the query.
        var searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(10)
                .minScore(0.7)
                .build();

        // Return the matching segments' text as context for the chat model.
        return myDB.search(searchRequest).matches().stream()
                .map(EmbeddingMatch::embedded)
                .map(TextSegment::text)
                .toList();
    }

    private static void loadEmbeddingsFromFile(InMemoryEmbeddingStore<TextSegment> myDB, EmbeddingModel eModel,
            String filePath) {
        Path path = Path.of(filePath);
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("RAG source file does not exist or is not a file: " + path);
        }

        Document document = FileSystemDocumentLoader.loadDocument(path);
        // Keep each non-blank line as its own segment instead of combining lines into
        // token-sized chunks.
        List<TextSegment> segments = document.text().lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(TextSegment::from)
                .toList();
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("RAG source file contains no text: " + path);
        }

        // Store each embedding with its source segment so search can return the
        // matching text.
        List<Embedding> embeddings = eModel.embedAll(segments).content();
        myDB.addAll(embeddings, segments);
    }
}

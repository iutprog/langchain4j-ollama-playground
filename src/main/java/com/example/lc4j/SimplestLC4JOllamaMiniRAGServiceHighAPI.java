package com.example.lc4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 * - no API key: the model runs locally in Podman
 * - baseUrl instead of a vendor endpoint
 * - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaMiniRAGServiceHighAPI {
    /**
     * Assistant
     */
    public interface Assistant {
        @SystemMessage("You are a polite and very detailed senior consultant")
        String send(String text);

    }

    private static final Scanner USER_INPUT = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String cmdLine;

        EmbeddingModel eModel = createEmbeddingModel();
        InMemoryEmbeddingStore<TextSegment> myDB = new InMemoryEmbeddingStore<>(); // in-memory embedding store

        List<String> lines = Files.readAllLines(Paths.get("src/main/java/com/example/hotline/firstlevel"));
        List<Document> documents = convertLinesToDocuments(lines); // See two implementations below

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(new DocumentBySentenceSplitter(512, 25))
                .embeddingStore(myDB)
                .embeddingModel(eModel)
                .build();

        ingestor.ingest(documents);

        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(eModel)
                .embeddingStore(myDB)
                .maxResults(5) // At most return this number of results
                .minScore(0.7) // Return results that relevant (0 - > 1 least ->most)
                .build();

        ChatModel cModel = createChatModel();

        Assistant cb = AiServices.builder(Assistant.class)
                .chatModel(cModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        while (true) {
            if ((cmdLine = getInput("Cmd>")).isEmpty())
                continue;

            var response = cb.send(cmdLine); // send everything to LLM
            System.out.println(response);
        }

    }

    private static List<Document> convertLinesToDocuments(List<String> lines) {
        List<Document> documents = lines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(Document::from)
                .toList();

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Cannot create documents: the input contains no non-blank lines");
        }
        return documents;
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

}

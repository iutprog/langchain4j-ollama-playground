package com.example.lc4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 * - no API key: the model runs locally in Podman
 * - baseUrl instead of a vendor endpoint
 * - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaChatWithTemplate {

    private static final Scanner USER_INPUT = new Scanner(System.in);

    public static void main(String[] args) {

        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();

        String myTemplate = "Translate {{mytext}} from English to {{language}} and then back to English. Show the delta between the original English and your translated English.";
        PromptTemplate promptTemplate = PromptTemplate.from(myTemplate);

        while (true) {
            String text = getUserInput("Text> ");
            String language = getUserInput("Language> ");

            Map<String, Object> variables = new HashMap<>();
            variables.put("mytext", text);
            variables.put("language", language);

            Prompt prompt = promptTemplate.apply(variables);
            System.out.println("ORIGINAL: " + text);
            System.out.println("====================================");
            System.out.println(prompt.text());
            System.out.println("====================================");

            String response = cmodel.chat(prompt.text());
            System.out.println(response);

        }

    }

    private static String getUserInput(String input) {
        System.out.print(input);
        return USER_INPUT.nextLine();
    }
}

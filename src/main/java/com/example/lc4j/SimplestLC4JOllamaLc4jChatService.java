package com.example.lc4j;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import java.time.Duration;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaLc4jChatService {
        /**
         * Chatbot
         */
        public interface ChatBot {
          String sendChat(String text);
        }
    public static void main(String[] args) {
        Scanner userInput;
        String cmdLine;

        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();
        
        ChatBot chatBot = AiServices.builder(ChatBot.class)
                           .chatModel(cmodel)
                           .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) //Add ChatMemory
                           .build();
        
        while (true) {
            System.out.println("prompt> ");

            userInput = new Scanner(System.in);
            cmdLine = userInput.nextLine();

            if(cmdLine.isBlank())  //If nothing, do nothing.
                continue;
            System.out.println(chatBot.sendChat(cmdLine));
        }
    }
}

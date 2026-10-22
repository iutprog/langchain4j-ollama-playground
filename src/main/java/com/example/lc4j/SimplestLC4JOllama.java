package com.example.lc4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllama {
    public static void main(String[] args) {
        Scanner userInput;
        String cmdLine;

        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();

        while(true){
                System.out.println("Prompt>");

                userInput = new Scanner(System.in);
                cmdLine = userInput.nextLine();

                if (cmdLine.isBlank())
                    continue;

                UserMessage userMessage = UserMessage.from(cmdLine);
                var answer = cmodel.chat(userMessage);
                System.out.println(answer.aiMessage().text());
        }
                
    }
}

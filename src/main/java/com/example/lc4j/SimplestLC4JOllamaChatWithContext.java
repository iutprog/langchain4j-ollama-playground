package com.example.lc4j;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;


import java.time.Duration;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaChatWithContext {

    public static void main(String[] args) {
        Scanner userInput;
        String cmdLine;
        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(100);

        while(true){
                System.out.println("Prompt>");

                userInput = new Scanner(System.in);
                cmdLine = userInput.nextLine();

                if (cmdLine.isBlank())
                    continue;

                UserMessage userMessage = UserMessage.from(cmdLine); // create the prompt
                chatMemory.add(userMessage);

                var answer = cmodel.chat(chatMemory.messages());
                var response = answer.aiMessage().text();
                 
                System.out.println(response);

                chatMemory.add(UserMessage.from(response)); //Add response from LLM.
                chatMemory.add(UserMessage.from(cmdLine));  //Add prompt from the user.
        }
                
    }
}

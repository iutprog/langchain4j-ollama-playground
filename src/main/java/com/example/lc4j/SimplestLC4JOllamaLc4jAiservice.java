package com.example.lc4j;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.AiServices;
import java.time.Duration;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaLc4jAiservice {
        /**
         * Consultant
         */
        public interface Consultant {
          @SystemMessage("You are a polite technology consultant")
          @UserMessage("What are the benefits of Java in 5 lines")
          String consult();

          @SystemMessage("You are a hilariously funny junior developer")
          String funny(String text);
        }
    public static void main(String[] args) {
        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();

         
        Consultant consultant = AiServices.create(Consultant.class, cmodel);
        System.out.println("PROFESSIONAL==============================================");
        System.out.println(consultant.consult());
        System.out.println("COMEDIAN==================================================");
        System.out.println(consultant.funny("What are the benefits of Java in 4 lines."));        
    }
}

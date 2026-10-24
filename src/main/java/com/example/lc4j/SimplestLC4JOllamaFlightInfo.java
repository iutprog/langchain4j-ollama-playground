package com.example.lc4j;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Same tools as the course's FlightInfoTools. Changes:
 *   - logger uses FlightInfoTools.class (the original pointed at a different class, FlightInfo)
 *   - @P describes the parameter: small local models need it more than GPT-4o does
 * The @Tool text, method name and parameter description are ALL the model sees.
 * It never sees the method body.
 */

public class SimplestLC4JOllamaFlightInfo {

   /**
    * FlightAssistant
    */
   interface FlightAssistant {
       String flightInfo(String userMessage);     
   }

   public static void main(String[] args){
      ChatModel cmodel = OllamaChatModel.builder()
            .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
            .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
            .timeout(Duration.ofSeconds(600))
            //.strictTools(true)
            .logRequests(true)
            .build();
      ChatMemory cMemory = MessageWindowChatMemory.withMaxMessages(10);
      cMemory.add(SystemMessage.from("You are helpful, and informative flight agent. Only use the methos I have described."));

      FlightAssistant assistant = AiServices.builder(FlightAssistant.class)
             .chatModel(cmodel)
             .tools(new SimplestLC4JOllamaFlightInfoTools())
             .chatMemory(cMemory)  // An effective system message that controls/constrains the method choices is critical.
             .build();

      String response;
      response = assistant.flightInfo("I need the status of Flight UA1011");
      System.out.println(response);

      response = assistant.flightInfo("What type of aircraft is it ?");
      System.out.println(response);

      response = assistant.flightInfo("What was its cost ?");
      System.out.println(response);
   }
}

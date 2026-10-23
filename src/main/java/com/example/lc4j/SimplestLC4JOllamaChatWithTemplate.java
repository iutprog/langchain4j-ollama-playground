package com.example.lc4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaChatWithTemplate {


    public static void main(String[] args) {

 
        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build(); 

        String myTemplate = 
             "Please explain {{topic}} to a {{student_type}} using clear, concise paragraph";
        PromptTemplate promptTemplate = PromptTemplate.from(myTemplate);
        
        Map<String, Object> variables  = new HashMap<>();
        variables.put("topic", "quantum computing");
        variables.put("student_type", "musician");

        Prompt  prompt = promptTemplate.apply(variables);
        
        String response = cmodel.chat(prompt.text());
        System.out.println(response);
        /* 
        List<String> myFiles = Arrays.asList(folder+"firstlevel");
        String fileString = filesToStrings(myFiles);

        while(true){
                System.out.println("Prompt>");

                userInput = new Scanner(System.in);
                cmdLine = userInput.nextLine();

                if (cmdLine.isBlank())
                    continue;


                userMessages.add(UserMessage.from(fileString));
                userMessages.add(UserMessage.from(cmdLine));
                var answer = cmodel.chat(userMessages);
                System.out.println(answer.aiMessage().text());
        } */
                
    }
}

package com.example.lc4j;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaChatWithTemplate {


    public static void main(String[] args) {

/* 
        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build(); */

        String formattedString = 
              String.format("Translate '%s' into %s", "My name is frank", "Japanese");
        System.out.println(formattedString);
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

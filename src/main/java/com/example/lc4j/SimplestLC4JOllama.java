package com.example.lc4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 *   - no API key: the model runs locally in Podman
 *   - baseUrl instead of a vendor endpoint
 *   - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllama {
    private static String filesToStrings(List<String> files) {
        StringBuilder contents = new StringBuilder();
        for (String file : files) {
            try {
                contents.append(Files.readString(Path.of(file)))
                        .append(System.lineSeparator());
            } catch (IOException e) {
                throw new UncheckedIOException("Could not read file: " + file, e);
            }
        }
        return contents.toString();
    }

    public static void main(String[] args) {
        Scanner userInput;
        String cmdLine;
        String folder = "src/main/java/com/example/hotline/";
        List<ChatMessage> userMessages = new ArrayList<>();

        ChatModel cmodel = OllamaChatModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2"))
                .timeout(Duration.ofSeconds(600))
                .build();

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
        }
                
    }
}

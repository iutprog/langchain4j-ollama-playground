package com.example.lc4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.time.Duration;
import java.util.List;

/**
 * The Ollama twin of SimplestLC4J (OpenAI version). Differences:
 * - no API key: the model runs locally in Podman
 * - baseUrl instead of a vendor endpoint
 * - modelName is a plain string that must match `ollama list`
 */
public class SimplestLC4JOllamaGetEmbeddingsCosineSimilarity {

    public static void main(String[] args) {
        EmbeddingModel eModel = OllamaEmbeddingModel.builder()
                .baseUrl(System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://[::1]:11434"))
                .modelName(System.getenv().getOrDefault("OLLAMA_MODEL", "nomic-embed-text"))
                .timeout(Duration.ofSeconds(600))
                .build();
        
        List<Float> one = getEmbeddingVec(eModel, "I like programming using Java");
        List<Float> two = getEmbeddingVec(eModel, "I enjoy writing Java programs");
        double similarity = cosineSimilarity(FloatList2doubleArray(one), FloatList2doubleArray(two));

        System.out.println("Consine Similarity: " + similarity);

    }

    private static double[] FloatList2doubleArray(List<Float> values) {
        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private static List<Float> getEmbeddingVec(EmbeddingModel eModel, String string) {
        Response<Embedding> response = eModel.embed(string);
        return response.content().vectorAsList();
    }

    private static double cosineSimilarity(double[] one, double[] two) {
        if (one.length != two.length) {
            throw new IllegalArgumentException("Embedding vectors must have the same dimension");
        }
        if (one.length == 0) {
            throw new IllegalArgumentException("Embedding vectors must not be empty");
        }

        double dotProduct = 0.0;
        double oneMagnitudeSquared = 0.0;
        double twoMagnitudeSquared = 0.0;
        for (int i = 0; i < one.length; i++) {
            dotProduct += one[i] * two[i];
            oneMagnitudeSquared += one[i] * one[i];
            twoMagnitudeSquared += two[i] * two[i];
        }

        double magnitudeProduct = Math.sqrt(oneMagnitudeSquared) * Math.sqrt(twoMagnitudeSquared);
        if (magnitudeProduct == 0.0) {
            throw new IllegalArgumentException("Cosine similarity is undefined for a zero vector");
        }
        return dotProduct / magnitudeProduct;
    }
}

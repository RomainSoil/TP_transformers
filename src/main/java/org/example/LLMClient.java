package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LLMClient {

    private final String model;

    public LLMClient(String model) {
        this.model = model;
    }

    public String ask(String prompt, double temperature) throws Exception {

        String json = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"temperature\":%.2f}",
                model, prompt.replace("", "\\"), temperature
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}

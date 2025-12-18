package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Meteo {

    private static final String API_KEY = "VOTRE_CLE_API";

    public static class MeteoInfo {
        public double temperature;
        public String description;
    }

    public MeteoInfo getWeather(String city) throws Exception {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=fr&appid=%s",
                city, API_KEY
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parsing JSON très simplifié (pour TP)
        MeteoInfo info = new MeteoInfo();
        String body = response.body();

        info.temperature = Double.parseDouble(body.split(""temp":")[1].split(",")[0]);
        info.description = body.split(""description":"")[1].split(""")[0];

        return info;
    }
}

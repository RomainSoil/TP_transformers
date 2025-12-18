package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== LLMifier - Recettes selon météo ===");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez votre ville : ");
        String city = scanner.nextLine();

        System.out.println("Exprimez vos préférences alimentaires : ");
        String preferences = scanner.nextLine();

        try {
            Meteo meteo = new Meteo();
            Meteo.MeteoInfo info = meteo.getWeather(city);

            String season = SeasonUtils.getCurrentSeason();

            String prompt = buildPrompt(info, season, preferences);

            LLMClient llm = new LLMClient("phi-3");
            String response = llm.ask(prompt, 0.7);

            System.out.println("\n--- Menu proposé ---");
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    private static String buildPrompt(Meteo.MeteoInfo info, String season, String preferences) {
        return String.format(
                "Saison: %s\nTempérature: %.1f°C\nClimat: %s\nPréférences: %s\nPropose un menu.",
                season, info.temperature, info.description, preferences
        );
    }
}

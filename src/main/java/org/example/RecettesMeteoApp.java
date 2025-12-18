package org.example;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Scanner;

public class RecettesMeteoApp {

    public static void main(String[] args) throws Exception {
        // Initialiser le client HTTP pour LLMTest
        LLMTest.baseUrl = "http://localhost:11434";
        LLMTest.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        Scanner sc = new Scanner(System.in);

        // Demande ville
        System.out.print("Entrez votre ville : ");
        String ville = sc.nextLine().trim();

        // Récupérer météo
        Meteo meteoService = new Meteo();
        Meteo.WeatherData wd = meteoService.getWeatherByCity(ville);

        if (wd == null) {
            System.out.println("Impossible de récupérer la météo. Vérifie la ville et ta clé API.");
            return;
        }

        // Saison
        String saison = getSaison(LocalDate.now());

        // Construire l'historique des préférences
        String[] history = new String[] {
                "je n'aime pas les choux que proposez vous par 10°",
                "je propose une soupe de patates douces",
                "je n'aime pas les champignons",
        };

        // prompt
        String systemPrompt =
                "Tu es un assistant cuisine. " +
                        "Tu dois proposer un menu adapté à la saison et à la météo. " +
                        "Tu dois respecter STRICTEMENT les préférences de l'utilisateur données dans l'historique. " +
                        "Réponds en français. " +
                        "Format obligatoire:\n" +
                        "Entrée: ...\nPlat: ...\nDessert: ...\n" +
                        "Puis une courte justification en 2 phrases max.";

        // Message utilisateur du moment (avec météo + saison)
        String userMessage = buildUserMessage(saison, wd);

        // LLM
//        String model = "gpt-oss:120b-cloud";
//        String model = "tinyllama";
        String model = "Phi4-mini";


        // LLM avec historique
        String reponse = LLMTest.chatWithHistory(model, systemPrompt, userMessage, history);

        // Affichage
        System.out.println("\n=== Données météo ===");
        System.out.println("Ville : " + wd.getCityName());
        System.out.println("Température : " + wd.getTemperature() + " °C");
        System.out.println("Condition principale : " + wd.getMainCondition());
        System.out.println("Description : " + wd.getDescription());


        System.out.println("\n=== Menu proposé par le LLM ===");
        System.out.println(reponse);

        sc.close();
    }

    private static String getSaison(LocalDate date) {
        int m = date.getMonthValue();
        if (m == 12 || m == 1 || m == 2) return "hiver";
        if (m >= 3 && m <= 5) return "printemps";
        if (m >= 6 && m <= 8) return "été";
        return "automne";
    }

    private static String buildUserMessage(String saison, Meteo.WeatherData wd) {
        // Extraits météo utiles
        double temp = wd.getTemperature();
        String condition = wd.getMainCondition();
        String desc = wd.getDescription();
        String ville = wd.getCityName();

        return "Voici les informations météo actuelles :\n"
                + "Je suis à " + ville + ".\n"
                + "Nous sommes en " + saison + ".\n"
                + "Météo actuelle: " + temp + "°C, condition=" + condition + ", description=" + desc + ".\n"
                + "Propose un menu (entrée/plat/dessert) adapté à la météo et à la saison, en respectant mes préférences.";
    }
}

package ollama;

import ollama.gui.GuiOllamaAgent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LLMTest  {
    static  HttpClient httpClient;
    static  String baseUrl;
    static String modelName;
    static String laMeteo = "tempere, 18°C";

    /**
     * this main launch JADE plateforme and asks it to create an agent
     */
    public static void main(String[] args) {
        setup();
        sample1();
    }

    /**
     * agent set-up
     */
    static void setup() {
        System.out.println("Hello! I'm able to use LLM models!");

        try {
        baseUrl = "http://localhost:11434";
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        String texteHello = "Hello everybody and especially you !";

        // Lister les modèles disponibles
        System.out.println("=== Available LLM models  ===");
        String[] models = listModels();
        for (String model : models) {
            System.out.println("- " + model);
        }
            modelName = models[2];
        } catch (Exception e) {e.printStackTrace();}


                //agent asks to be removed from the platform
        //doDelete();

    }
    /**
     * Méthode pour lister les modèles LLM disponibles sur la machine par l'API Ollama
     */
    static  String[] listModels() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/tags"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray modelsArray = jsonResponse.getJSONArray("models");

            String[] modelNames = new String[modelsArray.length()];
            for (int i = 0; i < modelsArray.length(); i++) {
                JSONObject model = modelsArray.getJSONObject(i);
                modelNames[i] = model.getString("name");
            }
            return modelNames;
        }
        return new String[0];
    }

    /**
     * Méthode pour générer une réponse simple (non chat) avec un modèle donné
     * @param model  le nom du modèle LLM à utiliser
     * @param prompt le texte d'entrée pour la génération
     * */
    static String generateResponse(String model, String prompt) throws Exception {
        // Construction du JSON avec org.json
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("model", model);
        jsonRequest.put("prompt", prompt);
        jsonRequest.put("stream", false);

        // Création de la requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest.toString()))
                .timeout(Duration.ofMinutes(5))
                .build();

        // Envoi de la requête
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Traitement de la réponse avec org.json
        if (response.statusCode() == 200) { //ok
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getString("response");
        } else {
            throw new RuntimeException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Méthode pour un chat avec historique
     * @param model            le nom du modèle LLM à utiliser
     * @param systemPrompt     le prompt système (instructions pour le modèle)
     * @param userMessage      le message utilisateur actuel
     * @param previousMessages un tableau de messages précédents (alternance personne/assistant)
     * */
    static String chatWithHistory(String model, String systemPrompt,
                                  String userMessage, String[] previousMessages) throws Exception {

        // Construction du JSON pour l'API chat
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("model", model);
        jsonRequest.put("stream", false);

        // Construction du tableau de messages
        JSONArray messages = new JSONArray();

        // Message système
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.put(systemMessage);
        }

        // Ajout des messages précédents (historique)
        if (previousMessages != null) {
            for (int i = 0; i < previousMessages.length; i += 2) {
                if (i + 1 < previousMessages.length) {
                    // Message utilisateur
                    JSONObject userMsg = new JSONObject();
                    userMsg.put("role", "user");
                    userMsg.put("content", previousMessages[i]);
                    messages.put(userMsg);

                    // Message assistant
                    JSONObject assistantMsg = new JSONObject();
                    assistantMsg.put("role", "assistant");
                    assistantMsg.put("content", previousMessages[i + 1]);
                    messages.put(assistantMsg);
                }
            }
        }

        // Message utilisateur actuel
        JSONObject currentUserMessage = new JSONObject();
        currentUserMessage.put("role", "user");
        currentUserMessage.put("content", userMessage);
        messages.put(currentUserMessage);

        jsonRequest.put("messages", messages);

        // Envoi de la requête
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest.toString()))
                .timeout(Duration.ofMinutes(5))
                .build();

        // normalement, la réponse tient compte de l'historique
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject message = jsonResponse.getJSONObject("message");
            return message.getString("content");
        } else {
            throw new RuntimeException("Erreur HTTP: " + response.statusCode());
        }
    }


    /**
     * * Méthode pour un chat simple sans historique
     * @param model        le nom du modèle LLM à utiliser
     * @param systemPrompt le prompt système (instructions pour le modèle)
     * @param userMessage  le message utilisateur actuel
     */
    static String simpleChat(String model, String systemPrompt, String userMessage) throws Exception {
        return chatWithHistory(model, systemPrompt, userMessage, null);
    }


    static void sample2()
    {
        try {
            // Test chat avec historique
            System.out.println("\n=== Test chat avec historique ===");
            System.out.println("(patientez quelques secondes si le modèle est volumineux)");
            String[] history = {
                    //TODO: intégrer un  historique d'échanges relatifs à la météo et la cuisine
            };
            System.out.println("Historique forcée :");
            for(int i=0; i<history.length; i+=2)
                System.out.println("User: %s  --> Assistant: %s".formatted(history[i], history[i+1]));
            System.out.println("?".repeat(20));
            String historyResponse = chatWithHistory(modelName,
                    "Tu es un assistant sympathique",
                    "Comment cuisiner le repas ?",
                    history);
            System.out.println( historyResponse);
            System.out.println("~".repeat(50));
        }
        catch (Exception e) {e.printStackTrace();}
    }


    private String demanderMeteo(String ville) {
        //TODO: demander la météo à l'API meteo
        laMeteo = "ensoleillé, 25°C";
        return laMeteo;
    }

    /**
     * * Exemple d'utilisation des différentes méthodes
     * */
    static void sample1() {
        System.out.println("~".repeat(50));
        // Test génération simple
        String texte = """ 
                        during a vote for a restaurant, the following results were obtained :
                        ----------------------------------------
                Pizza obtained 23 points
                Vegetables obtained 35 points
                RedGrill obtained 31 points
                Sushi obtained 28 points
                FishAndFish obtained 33 points
                        ----------------------------------------
                [[[ Voting result [Vegetables]]]]
                """;
        try {
            var prompt = "give a summary of this vote : the top 3 results, and the winner choice: " + texte;
            System.out.println(prompt);
            //TOODO: tester des LLM pour une reponse simple et correcte
            String response = generateResponse(modelName, prompt);
            System.out.println("?".repeat(20));
            System.out.println("Réponse: " + response);

            System.out.println("~".repeat(50));

        }
        catch (Exception e) {e.printStackTrace();}
    }

}

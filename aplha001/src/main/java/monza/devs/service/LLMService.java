package monza.devs.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class LLMService {
    
    private static final OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(40, TimeUnit.SECONDS)
    .readTimeout(40, TimeUnit.SECONDS)
    .writeTimeout(40, TimeUnit.SECONDS)
    .build();
    /**
     * Sends a prompt to the Ollama API and returns the generated response.
     * <p>
     * This method constructs a JSON payload with the specified model, prompt, and 
     * a stream flag set to {@code false}. It then sends a POST request to the 
     * provided Ollama API endpoint and extracts the "response" field from the JSON reply.
     * </p>
     *
     * @param MODEL           The name of the Ollama model to use (e.g., "llama2", "mistral").
     * @param userPrompt      The natural language input to send to the model.
     * @param OLLAMA_API_URL  The full URL of the Ollama API endpoint (e.g., "http://localhost:11434/api/generate").
     * @return The string content of the "response" field returned by the Ollama API,
     *         or {@code "Default"} if an error occurs or the response is missing.
     *
     * @throws RuntimeException If there is a critical error parsing the JSON response.
     *
     * @example
     * <pre>{@code
     * String response = getOllamaResponse("llama2", "Tell me a joke.", "http://localhost:11434/api/generate");
     * System.out.println(response);
     * }</pre>
     */
    public String getOllamaResponse(String MODEL, String OLLAMA_API_URL, String userPrompt) {
        RequestBody body = null;
        try {
            JSONObject promptJson = new JSONObject();
            promptJson.put("model", MODEL);
            promptJson.put("prompt", userPrompt);
            promptJson.put("stream", false);
            String prompt = promptJson.toString();
            // System.out.println(prompt);
            body = RequestBody.create(prompt, MediaType.get("application/json"));
        } catch (org.json.JSONException e) {
            throw new RuntimeException("Error while building JSON request body: " + e.getMessage(), e);
        }

        // Build HTTP request
        Request request = new Request.Builder()
                .url(OLLAMA_API_URL)
                .post(body)
                .build();

        // Initialize default response
        String rawResponse = "{\"response\": \"No response recieved from ollama\"}";

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            // System.out.println("Response Code: " + response.code());
            if (response.body() != null) {
                rawResponse = response.body().string();
            }
        } catch (IOException e) {
            System.err.println("Error while calling Ollama API: " + e.getMessage());
        }

        // Parse JSON and return extracted response
        try {
            JSONObject jsonResponse = new JSONObject(rawResponse);
            return jsonResponse.optString("response", "No response recieved from ollama");
        } catch (org.json.JSONException e) {
            System.err.println("Error while parsing JSON response: " + e.getMessage());
            return "No response recieved from ollama";
        }
        // return rawResponse;
    }
    
}
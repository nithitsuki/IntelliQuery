package monza.devs;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SpringBootApplication
public class App {
    final static String MODEL = "deepseek-coder-v2:latest";
    final static String OLLAMA_API_URL = "http://localhost:11434/api/generate";

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(40, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // System.out.print("Enter your prompt (type 'exit' to quit): ");
            System.out.print("SQL CONVERTER 5000!\nEnter your prompt (type 'exit' to quit): ");
            String userPrompt = scanner.nextLine();

            if (userPrompt.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            JSONObject NLP_PROMPT_JSON = new JSONObject();
            try {
                NLP_PROMPT_JSON.put("query", userPrompt);
                NLP_PROMPT_JSON.put("model", MODEL);  // lowercase
                NLP_PROMPT_JSON.put("ollama_api_url", OLLAMA_API_URL);  // lowercase
            } catch (org.json.JSONException e) {
                System.err.println("⚠️ Failed to construct JSON object: " + e.getMessage());
                continue;
            }
            

            RequestBody body = RequestBody.create(
                    NLP_PROMPT_JSON.toString(),
                    MediaType.get("application/json"));

            System.out.println(NLP_PROMPT_JSON.toString());

            Request request = new Request.Builder()
                    .url("http://localhost:8080/to-sql")
                    .post(body)
                    .build();
                    
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body() != null) {
                        try {
                            JSONObject responseJson = new JSONObject(response.body().string());
                            System.out.println(responseJson.optString("sql", "No SQL returned."));
                        } catch (org.json.JSONException e) {
                            System.err.println("⚠️ Failed to parse JSON response: " + e.getMessage());
                        }
                    } else {
                        System.out.println("❌ Response body is null.");
                    }
                } else {
                    System.out.println("❌ Error from server: " + response.code());
                }
            } catch (IOException e) {
                System.err.println("⚠️ Failed to call /to-sql endpoint: " + e.getMessage());
            }
        }
        scanner.close();
    }
}

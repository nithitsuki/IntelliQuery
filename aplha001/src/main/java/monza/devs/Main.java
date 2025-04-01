package monza.devs;

import java.io.IOException;
import java.util.*;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main {
    final static String Model = "tinyllama";
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your prompt (type 'exit' to quit): ");
            String userPrompt = scanner.nextLine();

            if (userPrompt.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }
            
            String prompt = "{\"model\": \"";
            prompt += Model;
            prompt += "\", ";
            prompt += "\"prompt\": \"";
            prompt += userPrompt; // enter prompt here
            prompt += "\", \"stream\": false}";
            RequestBody body = RequestBody.create(prompt, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url("http://localhost:11434/api/generate")
                    .post(body)
                    .build();
            String RB = "{\"response\": \"Default\"}";
            try (Response response = client.newCall(request).execute()) {
                System.out.println("Response Code: " + response.code());
                RB = response.body().string();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            JSONObject jsonResponse = new JSONObject(RB);
            String extractedResponse = jsonResponse.getString("response");
            System.out.println("Extracted Response: " + extractedResponse);
        }
        scanner.close();
    }
}

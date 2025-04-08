package monza.devs;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import monza.devs.service.LLMService;
import okhttp3.OkHttpClient;

@SpringBootApplication
public class App {
    final static String MODEL = "deepseek-coder-v2";
    final static String OLLAMA_API_URL = "http://localhost:11434/api/generate";

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        OkHttpClient client = new OkHttpClient();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your prompt (type 'exit' to quit): ");
            String userPrompt = scanner.nextLine();

            if (userPrompt.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }
            
            String response = LLMService.getOllamaResponse(MODEL, userPrompt, OLLAMA_API_URL);
            
            System.out.println("Ollama says: " + response);

        }
        scanner.close();
    }
}

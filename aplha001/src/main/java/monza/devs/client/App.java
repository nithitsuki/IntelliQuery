package monza.devs.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class App extends Application {

    private TextField inputField;
    private TextField userPromptField;
    private TextField sqlField;
    private TextArea sqlOutputArea;
    private TextArea explanationArea;

    @Override
    public void start(Stage primaryStage) {
        // Title and input
        Label promptLabel = new Label("Enter your natural language query:");

        inputField = new TextField();
        inputField.setPromptText("e.g., Show all employees who joined after 2023");

        // Button to send query
        Button sendButton = new Button("Send to Backend");
        sendButton.setOnAction(e -> handleSend());

        // Output fields
        sqlField = new TextField();
        sqlField.setEditable(false);
        sqlField.setPromptText("Generated SQL");

        sqlOutputArea = new TextArea();
        sqlOutputArea.setEditable(false);
        sqlOutputArea.setWrapText(true);
        sqlOutputArea.setPromptText("SQL Query Output");

        explanationArea = new TextArea();
        explanationArea.setEditable(false);
        explanationArea.setWrapText(true);
        explanationArea.setPromptText("LLM Explanation");

        userPromptField = new TextField();
        userPromptField.setEditable(false);
        userPromptField.setPromptText("User Prompt");

        // Layout: top input
        VBox inputBox = new VBox(5, promptLabel, inputField, sendButton);
        inputBox.setPadding(new Insets(10));

        // Layout: userPrompt and SQL fields
        GridPane topOutputs = new GridPane();
        topOutputs.setHgap(10);
        topOutputs.setVgap(10);
        topOutputs.setPadding(new Insets(10));
        topOutputs.add(new Label("User Prompt:"), 0, 0);
        topOutputs.add(userPromptField, 1, 0);
        topOutputs.add(new Label("Generated SQL:"), 0, 1);
        topOutputs.add(sqlField, 1, 1);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(100); // for labels

        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS); // this lets the TextFields expand horizontally

        topOutputs.getColumnConstraints().addAll(labelColumn, fieldColumn);

        // Layout: bottom large boxes
        HBox bottomOutputs = new HBox(10, sqlOutputArea, explanationArea);
        bottomOutputs.setPadding(new Insets(10));
        HBox.setHgrow(sqlOutputArea, Priority.ALWAYS);
        HBox.setHgrow(explanationArea, Priority.ALWAYS);

        // Final layout
        VBox root = new VBox(10, inputBox, topOutputs, bottomOutputs);
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());

        primaryStage.setTitle("IntelliQuery — Natural Language to SQL");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

private void handleSend() {
    String query = inputField.getText().trim();
    if (query.isEmpty()) {
        explanationArea.setText("⚠️ Please enter a query before sending.");
        return;
    }

    userPromptField.setText(query);

    // Make HTTP request to backend
    
    // new Thread(() -> {
    //     OkHttpClient client = new OkHttpClient();

    //     // Create JSON body
    //     JSONObject json = new JSONObject();
    //     try {
    //         json.put("query", query);
    //         json.put("model", "deepseek-coder-v2");
    //         json.put("ollama_api_url", "http://localhost:11434/api/generate");
    //     } catch (org.json.JSONException e) {
    //         javafx.application.Platform.runLater(() ->
    //             sqlField.setText("❌ JSON creation error: " + e.getMessage())
    //         );
    //         return;
    //     }

    //     // Build request
    //     Request request = new Request.Builder()
    //         .url("http://localhost:8080/to-sql")
    //         .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
    //         .build();

    //     try (Response response = client.newCall(request).execute()) {
    //         String body = response.body() != null ? response.body().string() : "(No response)";
    //         System.out.println("Response: " + body); // Debugging line
    //         // String body = response.body() != null ? response.body().string() : "(No response)";
    //         Pattern pattern = Pattern.compile("```sql\\\\n(.*?)\\\\n```");
    //         Matcher matcher = pattern.matcher(body);            
    //         String extractedSQL = matcher.find() ? matcher.group(1) : "(No SQL found)";
    //         String finalSQL = extractedSQL;
    //         javafx.application.Platform.runLater(() -> sqlField.setText(finalSQL));
    //     } catch (IOException e) {
    //         javafx.application.Platform.runLater(() ->
    //             sqlField.setText("❌ HTTP error: " + e.getMessage())
    //         );

            
    //     }
    // }).start();
    new Thread(() -> {
        OkHttpClient client = new OkHttpClient();
    
        // Create JSON body for /to-sql
        JSONObject json = new JSONObject();
        try {
            json.put("query", query);
            json.put("model", "deepseek-coder-v2");
            json.put("ollama_api_url", "http://localhost:11434/api/generate");
        } catch (org.json.JSONException e) {
            javafx.application.Platform.runLater(() ->
                sqlField.setText("❌ JSON creation error: " + e.getMessage())
            );
            return;
        }
    
        // Step 1: Get SQL from LLM
        Request request = new Request.Builder()
            .url("http://localhost:8080/to-sql")
            .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
            .build();
    
        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "(No response)";
            System.out.println("Response: " + body); // Debugging line
    
            Pattern pattern = Pattern.compile("```sql\\\\n(.*?)\\\\n```");
            Matcher matcher = pattern.matcher(body);
            String extractedSQL = matcher.find() ? matcher.group(1) : "(No SQL found)";
            String finalSQL = extractedSQL;
    
            // Show SQL in the UI
            javafx.application.Platform.runLater(() -> sqlField.setText(finalSQL));
    
            // Step 2: Send SQL to /execute
            JSONObject execJson = new JSONObject();
            try {
                execJson.put("sql-query", finalSQL);
            } catch (org.json.JSONException e) {
                javafx.application.Platform.runLater(() ->
                    sqlOutputArea.setText("❌ SQL exec JSON error: " + e.getMessage())
                );
                return;
            }
    
            Request execRequest = new Request.Builder()
                .url("http://localhost:8080/execute")
                .post(RequestBody.create(execJson.toString(), MediaType.parse("application/json")))
                .build();
    
            try (Response execResponse = client.newCall(execRequest).execute()) {
                String execBody = execResponse.body() != null ? execResponse.body().string() : "(No DB response)";
                // javafx.application.Platform.runLater(() -> sqlOutputArea.setText(execBody));
                try {
    JSONObject responseJson = new JSONObject(execBody);
    if (responseJson.has("result")) {
        JSONArray resultArray = responseJson.getJSONArray("result");

        if (resultArray.length() > 0) {
            JSONObject firstRow = resultArray.getJSONObject(0);
            List<String> headers = new ArrayList<>(firstRow.keySet());

            // Step 1: Figure out max width per column
            Map<String, Integer> columnWidths = new HashMap<>();
            for (String header : headers) {
                columnWidths.put(header, header.length());
            }

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject row = resultArray.getJSONObject(i);
                for (String header : headers) {
                    int cellLength = row.optString(header, "").length();
                    columnWidths.put(header, Math.max(columnWidths.get(header), cellLength));
                }
            }

            // Step 2: Build formatted output
            StringBuilder formatted = new StringBuilder();

            // Print headers
            for (String header : headers) {
                formatted.append(String.format("%-" + (columnWidths.get(header) + 2) + "s", header));
            }
            formatted.append("\n");

            // Print separator
            for (String header : headers) {
                formatted.append("-".repeat(columnWidths.get(header))).append("  ");
            }
            formatted.append("\n");

            // Print rows
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject row = resultArray.getJSONObject(i);
                for (String header : headers) {
                    String value = row.optString(header, "");
                    formatted.append(String.format("%-" + (columnWidths.get(header) + 2) + "s", value));
                }
                formatted.append("\n");
            }

            javafx.application.Platform.runLater(() -> sqlOutputArea.setText(formatted.toString()));
        } else {
            javafx.application.Platform.runLater(() -> sqlOutputArea.setText("(No results)"));
        }
    } else {
        javafx.application.Platform.runLater(() -> sqlOutputArea.setText("(No 'result' key in response)"));
    }
} catch (Exception ex) {
    javafx.application.Platform.runLater(() -> sqlOutputArea.setText("❌ JSON parse error: " + ex.getMessage()));
}

            } catch (IOException e) {
                javafx.application.Platform.runLater(() ->
                    sqlOutputArea.setText("❌ DB exec error: " + e.getMessage())
                );
            }
    
        } catch (IOException e) {
            javafx.application.Platform.runLater(() ->
                sqlField.setText("❌ HTTP error: " + e.getMessage())
            );
        }
    }).start();
    
}
    public static void main(String[] args) {
        launch(args);
    }
}

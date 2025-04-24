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
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

        // Input field with button inside
        inputField = new TextField();
        inputField.setPrefHeight(60); // Set the preferred height to make the input field bigger
        inputField.setPromptText("e.g., Show all employees who joined after 2023");
        inputField.setStyle("-fx-prompt-text-fill: #676767;");

        // Button to send query
        Button sendButton = new Button("↑");
        sendButton.setStyle("-fx-background-radius: 50%; -fx-min-width: 55px; -fx-min-height: 55px; -fx-max-width: 55px; -fx-max-height: 55px; -fx-background-color: #676767; -fx-text-fill: #000000; -fx-font-size: 28px;");
        sendButton.setOnAction(e -> handleSend());

        // HBox to combine input field and button
        HBox inputContainer = new HBox(inputField, sendButton);
        inputContainer.setSpacing(5); // Add some spacing between the input field and button
        HBox.setHgrow(inputField, Priority.ALWAYS); // Allow the input field to grow

        // Layout: top input
        VBox inputBox = new VBox(5, promptLabel, inputContainer);
        inputBox.setPadding(new Insets(0));
        // Final layout with updated input box margins and height
        inputBox.setPadding(new Insets(10, 50, 0, 50)); // Add equal left and right margins


        // Output fields
        sqlField = new TextField();
        sqlField.setEditable(false);
        sqlField.setPromptText("Generated SQL");
        sqlField.setStyle("-fx-background-color: #212121; -fx-text-fill: white; -fx-prompt-text-fill: #676767;");

        sqlOutputArea = new TextArea();
        sqlOutputArea.setEditable(false);
        sqlOutputArea.setWrapText(true);
        sqlOutputArea.setPromptText("SQL Query Output");
        sqlOutputArea.setStyle("-fx-control-inner-background: #212121; -fx-text-fill: white; -fx-prompt-text-fill: #676767; -fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 0.3px; -fx-effect: null;");

        explanationArea = new TextArea();
        explanationArea.setEditable(false);
        explanationArea.setWrapText(true);
        explanationArea.setPromptText("LLM Explanation");
        explanationArea.setStyle("-fx-control-inner-background: #212121; -fx-text-fill: white; -fx-prompt-text-fill: #676767; -fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 0.3px; -fx-effect: null;");

        userPromptField = new TextField();
        userPromptField.setEditable(false);
        userPromptField.setPromptText("User Prompt");
        userPromptField.setStyle("-fx-background-color: #212121; -fx-text-fill: white; -fx-prompt-text-fill: #676767;");

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

        // Custom undecorated window with black titlebar
        HBox titleBar = new HBox();
        titleBar.setStyle("-fx-background-color: black;");
        titleBar.setPadding(new Insets(5));
        titleBar.setSpacing(10);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        // Somewhere to store the initial offset
                // Helper class to store x/y offset
                class Delta {double x, y;}
        
        final Delta dragDelta = new Delta();

        // Capture initial click offset from top-left of the window
        titleBar.setOnMousePressed(event -> {
            dragDelta.x = event.getScreenX() - primaryStage.getX();
            dragDelta.y = event.getScreenY() - primaryStage.getY();
        });

        // Update window position based on initial offset
        titleBar.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - dragDelta.x);
            primaryStage.setY(event.getScreenY() - dragDelta.y);
        });


        // Title label (centered using StackPane)
        Label titleLabel = new Label("IntelliQuery");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        // This StackPane will center the title
        StackPane titleWrapper = new StackPane(titleLabel);
        HBox.setHgrow(titleWrapper, Priority.ALWAYS);
        titleWrapper.setAlignment(Pos.CENTER);

        // Minimize button
        Button minimizeButton = new Button("⎯");
        minimizeButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));

        // Maximize/Restore button
        Button maximizeButton = new Button("☐");
        maximizeButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        maximizeButton.setOnAction(e -> {
            if (primaryStage.isMaximized()) {
                primaryStage.setMaximized(false);
            } else {
                primaryStage.setMaximized(true);
            }
        });

        // Close button
        Button closeButton = new Button("⨉");
        closeButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        closeButton.setOnAction(e -> primaryStage.close());

        for (Button btn : new Button[]{minimizeButton, maximizeButton, closeButton}) {
            btn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
            btn.setFocusTraversable(false); // avoid that annoying dotted outline
        }


        // Right-side button container
        HBox buttonBox = new HBox(minimizeButton, maximizeButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Add everything to titleBar
        titleBar.getChildren().addAll(titleWrapper, buttonBox);
        
        // Add title bar to the root layout
        VBox root = new VBox(titleBar);
        root.getChildren().addAll(topOutputs, bottomOutputs, inputBox);


        // VBox root = new VBox(10, topOutputs, bottomOutputs, inputBox);
        Scene scene = new Scene(root, 800, 600); // Increase overall height of the scene
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        final int RESIZE_MARGIN = 6;
        scene.setOnMouseMoved(event -> {
            boolean resizeH = event.getSceneX() > scene.getWidth() - RESIZE_MARGIN;
            boolean resizeV = event.getSceneY() > scene.getHeight() - RESIZE_MARGIN;
            if (resizeH && resizeV) {
                scene.setCursor(Cursor.SE_RESIZE);
            } else if (resizeH) {
                scene.setCursor(Cursor.E_RESIZE);
            } else if (resizeV) {
                scene.setCursor(Cursor.S_RESIZE);
            } else {
                scene.setCursor(Cursor.DEFAULT);
            }
        });

        scene.setOnMouseDragged(event -> {
            if (scene.getCursor() == Cursor.SE_RESIZE) {
                primaryStage.setWidth(event.getSceneX());
                primaryStage.setHeight(event.getSceneY());
            } else if (scene.getCursor() == Cursor.E_RESIZE) {
                primaryStage.setWidth(event.getSceneX());
            } else if (scene.getCursor() == Cursor.S_RESIZE) {
                primaryStage.setHeight(event.getSceneY());
            }
        });


        primaryStage.initStyle(StageStyle.UNDECORATED);
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
    final String LLM_MODEL = "deepseek-coder-v2";
    new Thread(() -> {
        OkHttpClient client = new OkHttpClient();
    
        // Create JSON body for /to-sql
        JSONObject json = new JSONObject();
        try {
            json.put("query", query);
            json.put("model", LLM_MODEL);
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
            System.out.println("Extracted SQL: " + finalSQL); // Debugging line

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

            // Step 3: Send request to /explain
        JSONObject explainJson = new JSONObject();
        try {
            explainJson.put("model", LLM_MODEL);
            explainJson.put("ollama_api_url", "http://localhost:11434/api/generate");
            explainJson.put("prompt", query);
            explainJson.put("query", finalSQL);
            explainJson.put("sql_output", resultArray.toString());
            
            System.out.println("Explain JSON: " + explainJson.toString()); // Debugging line
        } catch (org.json.JSONException e) {
            javafx.application.Platform.runLater(() -> 
                explanationArea.setText("❌ Explain JSON creation error: " + e.getMessage())
            );
            return;
        }
        javafx.application.Platform.runLater(() -> explanationArea.setText("🤖 AI thinking..."));

        OkHttpClient explainClient = new OkHttpClient.Builder()
            .callTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Set timeout to 60 seconds
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

        Request explainRequest = new Request.Builder()
            .url("http://localhost:8080/explain")
            .post(RequestBody.create(explainJson.toString(), MediaType.parse("application/json")))
            .build();

        try (Response explainResponse = explainClient.newCall(explainRequest).execute()) {
            if (explainResponse.body() != null) {
                JSONObject explainResponseJson = new JSONObject(explainResponse.body().string());
                String tempExplainBody = explainResponseJson.optString("explaination", "(No explanation found)");
                javafx.application.Platform.runLater(() -> explanationArea.setText(tempExplainBody));
            } else {
                javafx.application.Platform.runLater(() -> explanationArea.setText("(No explanation response)"));
            }
        } catch (IOException e) {
            javafx.application.Platform.runLater(() -> 
            explanationArea.setText("❌ Explain HTTP error: " + e.getMessage())
            );
        }
    }
    else {
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

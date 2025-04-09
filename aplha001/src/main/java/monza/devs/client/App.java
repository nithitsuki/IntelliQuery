package monza.devs.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private TextField inputField;
    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        // Title and input
        Label promptLabel = new Label("Enter your natural language query:");

        inputField = new TextField();
        inputField.setPromptText("e.g., Show all employees who joined after 2023");

        // Button to send query
        Button sendButton = new Button("Send to Backend");
        sendButton.setOnAction(e -> handleSend());

        // Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Backend response will appear here...");
        outputArea.setWrapText(true);

        // Layout
        VBox root = new VBox(10, promptLabel, inputField, sendButton, outputArea);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("IntelliQuery — Natural Language to SQL");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleSend() {
        String query = inputField.getText().trim();
        if (query.isEmpty()) {
            outputArea.setText("⚠️ Please enter a query before sending.");
            return;
        }

        // For now, just show what was typed
        outputArea.setText("✅ You entered: " + query + "\n(Backend call coming soon...)");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

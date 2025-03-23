package com.e_val.e_Val.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.e_val.e_Val.model.dto.AuthResponse;

import java.io.InputStream;
import java.net.URI;
@Controller
public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Create HTTP request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password)
            ))
            .build();

        // Execute in background thread
        // new Thread(() -> {
        //     Text statusLabel;
        //     try {
        //         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
        //         Platform.runLater(() -> {
        //             if (response.statusCode() == 200) {
        //                 statusLabel.setText("Login successful!");
        //                 statusLabel.setFill(Color.GREEN);
        //                 // Store JWT token & navigate to dashboard
        //                 String token = new Gson().fromJson(response.body(), AuthResponse.class).getToken();
        //                 SessionManager.setToken(token);
        //                 showDashboard();
        //             } else {
        //                 statusLabel.setText("Invalid credentials!");
        //                 statusLabel.setTextFill(Color.RED);
        //             }
        //         });
        //     } catch (Exception e) {
        //         Platform.runLater(() -> {
        //             statusLabel.setText("Connection error!");
        //             statusLabel.setTextFill(Color.RED);
        //         });
        //     }
        // }).start();
    }
    @FXML
    private TextField Field1;

    @FXML
    private TextField Field2;

    @FXML
    private Button Log;

    @FXML
    private Button Reg;

    @FXML
    private void LogPress() {
        System.out.println("Login Button Pressed");
    }

    @FXML
    private void RegPress() {
        System.out.println("Register Button Pressed");
    }
}

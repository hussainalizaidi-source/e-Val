package com.e_val.e_Val.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class EvalClientApp extends Application {
    private static ConfigurableApplicationContext springContext;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start Spring Boot in a background thread
        new Thread(() -> {
            springContext = SpringApplication.run(com.e_val.e_Val.EValApplication.class);
        }).start();

        // Load JavaFX UI
        Parent root = FXMLLoader.load(getClass().getResource("/client/views/login.fxml"));
        primaryStage.setTitle("EQuiz - JavaFX & Spring Boot");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Shutdown Spring Boot when JavaFX exits
        if (springContext != null) {
            springContext.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
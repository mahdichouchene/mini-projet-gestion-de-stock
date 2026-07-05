package com.example.demogestionstockisimm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/demogestionstockisimm/view/Login.fxml")
        );
        Scene scene = new Scene(loader.load(), 450, 350);
        stage.setTitle("Gestion de Stock - ISIMM");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void changerScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/com/example/demogestionstockisimm/view/" + fxmlPath)
        );
        double width = fxmlPath.equals("Login.fxml") ? 450 : 900;
        double height = fxmlPath.equals("Login.fxml") ? 350 : 650;
        Scene scene = new Scene(loader.load(), width, height);
        primaryStage.setScene(scene);
        primaryStage.setResizable(!fxmlPath.equals("Login.fxml"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
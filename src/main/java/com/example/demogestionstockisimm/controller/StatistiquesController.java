package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.Article;
import com.example.demogestionstockisimm.model.Service;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatistiquesController implements Initializable {

    @FXML private Label lblPlusConsomme;
    @FXML private Label lblParService;
    @FXML private ComboBox<Service> cbService;

    @FXML private TableView<Article> tableConsommation;
    @FXML private TableColumn<Article, String> colReference;
    @FXML private TableColumn<Article, String> colType;
    @FXML private TableColumn<Article, Integer> colTotal;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<Article> observableArticles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colReference.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getReference()))
        );
        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType().getLabel())
        );
        colTotal.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        dataStore.getTotalConsomme(data.getValue())
                ).asObject()
        );

        cbService.setItems(FXCollections.observableArrayList(dataStore.getServices()));
    }

    @FXML
    private void calculerPlusConsomme() {
        Article article = dataStore.getProduitPlusConsomme();
        if (article == null) {
            lblPlusConsomme.setText("Aucune commande validée trouvée !");
            lblPlusConsomme.setStyle("-fx-text-fill: red;");
        } else {
            int total = dataStore.getTotalConsomme(article);
            lblPlusConsomme.setText(
                    "Article réf. " + article.getReference() +
                            " (" + article.getType().getLabel() + ")" +
                            " — Total consommé : " + total + " unités"
            );
            lblPlusConsomme.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
        }
    }

    @FXML
    private void calculerParService() {
        Service service = cbService.getValue();
        if (service == null) {
            afficherErreur("Veuillez sélectionner un service !");
            return;
        }
        Article article = dataStore.getProduitPlusConsommeParService(service);
        if (article == null) {
            lblParService.setText("Aucune consommation pour ce service !");
            lblParService.setStyle("-fx-text-fill: red;");
        } else {
            int total = dataStore.getTotalConsomme(article);
            lblParService.setText(
                    "Service " + service.getNom().getLabel() +
                            " → Article réf. " + article.getReference() +
                            " (" + article.getType().getLabel() + ")" +
                            " — Total : " + total + " unités"
            );
            lblParService.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
        }
    }

    @FXML
    private void afficherConsommation() {
        List<Article> articles = dataStore.getArticles();
        observableArticles.setAll(articles);
        tableConsommation.setItems(observableArticles);
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
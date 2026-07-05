/*

package com.example.demogestionstockisimm.controller;


import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.Article;
import com.example.demogestionstockisimm.model.TypeArticle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleController implements Initializable {

    @FXML private TableView<Article> tableArticles;
    @FXML private TableColumn<Article, Integer> colId;
    @FXML private TableColumn<Article, Integer> colReference;
    @FXML private TableColumn<Article, TypeArticle> colType;
    @FXML private TableColumn<Article, LocalDate> colDatePeremption;
    @FXML private TableColumn<Article, Integer> colStockMinimal;

    @FXML private TextField tfReference;
    @FXML private ComboBox<TypeArticle> cbType;
    @FXML private DatePicker dpDatePeremption;
    @FXML private TextField tfStockMinimal;
    @FXML private TextField tfRecherche;
    @FXML private Label lblAlerte;
    @FXML private RadioButton rbOui;
    @FXML private RadioButton rbNon;
    @FXML private TableColumn<Article, Boolean> colCritique;
    private ToggleGroup toggleCritique;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<Article> observableArticles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDatePeremption.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));
        colStockMinimal.setCellValueFactory(new PropertyValueFactory<>("stockMinimal"));
        colCritique.setCellValueFactory(new PropertyValueFactory<>("critique"));
        toggleCritique = new ToggleGroup();
        rbOui.setToggleGroup(toggleCritique);
        rbNon.setToggleGroup(toggleCritique);


        cbType.setItems(FXCollections.observableArrayList(TypeArticle.values()));

        tableArticles.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> remplirFormulaire(newValue)
        );

        rafraichirTableau();
        verifierAlertes();
    }

    @FXML
    private void ajouter() {
        if (tfReference.getText().isEmpty() || cbType.getValue() == null
                || dpDatePeremption.getValue() == null || tfStockMinimal.getText().isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs !");
            return;
        }
        try {
            int reference = Integer.parseInt(tfReference.getText());
            int stockMinimal = Integer.parseInt(tfStockMinimal.getText());
            TypeArticle type = cbType.getValue();
            LocalDate date = dpDatePeremption.getValue();
            boolean critique = rbOui.isSelected();

            Article a = new Article(reference, type, date, stockMinimal, critique);
            dataStore.ajouterArticle(a);
            rafraichirTableau();
            viderFormulaire();
            verifierAlertes();
            afficherInfo("Article ajouté avec succès !");
        } catch (NumberFormatException e) {
            afficherErreur("Référence et stock minimal doivent être des nombres !");
        }
    }

    @FXML
    private void modifier() {
        Article selectionne = tableArticles.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un article !");
            return;
        }
        try {
            selectionne.setReference(Integer.parseInt(tfReference.getText()));
            selectionne.setType(cbType.getValue());
            selectionne.setDatePeremption(dpDatePeremption.getValue());
            selectionne.setStockMinimal(Integer.parseInt(tfStockMinimal.getText()));
            selectionne.setCritique(rbOui.isSelected());


            dataStore.modifierArticle(selectionne);
            rafraichirTableau();
            viderFormulaire();
            afficherInfo("Article modifié avec succès !");
        } catch (NumberFormatException e) {
            afficherErreur("Référence et stock minimal doivent être des nombres !");
        }
    }

    @FXML
    private void supprimer() {
        Article selectionne = tableArticles.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un article !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Supprimer l'article " + selectionne.getReference() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.supprimerArticle(selectionne.getId());
                rafraichirTableau();
                viderFormulaire();
                verifierAlertes();
            }
        });
    }

    @FXML
    private void rechercher() {
        String critere = tfRecherche.getText().trim();
        if (critere.isEmpty()) {
            rafraichirTableau();
            return;
        }
        List<Article> resultats = dataStore.rechercherArticles(critere);
        observableArticles.setAll(resultats);
        tableArticles.setItems(observableArticles);
    }

    @FXML
    private void afficherTout() {
        tfRecherche.clear();
        rafraichirTableau();
    }

    @FXML
    private void viderFormulaire() {
        tfReference.clear();
        cbType.setValue(null);
        dpDatePeremption.setValue(null);
        tfStockMinimal.clear();
        tableArticles.getSelectionModel().clearSelection();
        rbNon.setSelected(true);

    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void remplirFormulaire(Article a) {
        if (a == null) return;
        tfReference.setText(String.valueOf(a.getReference()));
        cbType.setValue(a.getType());
        dpDatePeremption.setValue(a.getDatePeremption());
        tfStockMinimal.setText(String.valueOf(a.getStockMinimal()));
        if (a.isCritique()) {
            rbOui.setSelected(true);
        } else {
            rbNon.setSelected(true);
        }
    }

    private void rafraichirTableau() {
        observableArticles.setAll(dataStore.getArticles());
        tableArticles.setItems(observableArticles);
    }

    private void verifierAlertes() {
        List<Article> alertes = dataStore.getArticlesEnAlerte();
        if (!alertes.isEmpty()) {
            lblAlerte.setText("⚠ " + alertes.size() + " article(s) en alerte de stock !");
        } else {
            lblAlerte.setText("");
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

 */

package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.Article;
import com.example.demogestionstockisimm.model.TypeArticle;
import com.example.demogestionstockisimm.dao.ArticleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleController implements Initializable {

    @FXML private TableView<Article> tableArticles;
    @FXML private TableColumn<Article, Integer> colId;
    @FXML private TableColumn<Article, Integer> colReference;
    @FXML private TableColumn<Article, String> colNom;      // AMELIORATION 2
    @FXML private TableColumn<Article, TypeArticle> colType;
    @FXML private TableColumn<Article, LocalDate> colDatePeremption;
    @FXML private TableColumn<Article, Integer> colStockMinimal;
    @FXML private TableColumn<Article, Boolean> colCritique;

    @FXML private TextField tfReference;
    @FXML private TextField tfNom;                          // AMELIORATION 2
    @FXML private ComboBox<TypeArticle> cbType;
    @FXML private DatePicker dpDatePeremption;
    @FXML private TextField tfStockMinimal;
    @FXML private TextField tfRecherche;
    @FXML private Label lblAlerte;
    @FXML private RadioButton rbOui;
    @FXML private RadioButton rbNon;
    private ToggleGroup toggleCritique;

    private DataStore dataStore = DataStore.getInstance();
    private ArticleDAO articleDAO = new ArticleDAO();       // AMELIORATION 1
    private ObservableList<Article> observableArticles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDatePeremption.setCellValueFactory(new PropertyValueFactory<>("datePeremption"));
        colStockMinimal.setCellValueFactory(new PropertyValueFactory<>("stockMinimal"));
        colCritique.setCellValueFactory(new PropertyValueFactory<>("critique"));

        toggleCritique = new ToggleGroup();
        rbOui.setToggleGroup(toggleCritique);
        rbNon.setToggleGroup(toggleCritique);

        cbType.setItems(FXCollections.observableArrayList(TypeArticle.values()));

        tableArticles.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> remplirFormulaire(newValue)
        );

        rafraichirTableau();
        verifierAlertes();
    }

    @FXML
    private void ajouter() {
        if (tfReference.getText().isEmpty() || tfNom.getText().isEmpty()
                || cbType.getValue() == null
                || dpDatePeremption.getValue() == null
                || tfStockMinimal.getText().isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs !");
            return;
        }
        try {
            int reference = Integer.parseInt(tfReference.getText());
            // AMELIORATION 1 : vérifier doublon référence
            if (articleDAO.existeReference(reference)) {
                afficherErreur("Un article avec la référence " + reference + " existe déjà !");
                return;
            }
            Article a = new Article(
                    reference,
                    tfNom.getText(),
                    cbType.getValue(),
                    dpDatePeremption.getValue(),
                    Integer.parseInt(tfStockMinimal.getText()),
                    rbOui.isSelected()
            );
            dataStore.ajouterArticle(a);
            rafraichirTableau();
            viderFormulaire();
            verifierAlertes();
            afficherInfo("Article ajouté avec succès !");
        } catch (NumberFormatException e) {
            afficherErreur("Référence et stock minimal doivent être des nombres !");
        }
    }

    @FXML
    private void modifier() {
        Article selectionne = tableArticles.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un article !");
            return;
        }
        try {
            selectionne.setReference(Integer.parseInt(tfReference.getText()));
            selectionne.setNom(tfNom.getText());
            selectionne.setType(cbType.getValue());
            selectionne.setDatePeremption(dpDatePeremption.getValue());
            selectionne.setStockMinimal(Integer.parseInt(tfStockMinimal.getText()));
            selectionne.setCritique(rbOui.isSelected());
            dataStore.modifierArticle(selectionne);
            rafraichirTableau();
            viderFormulaire();
            afficherInfo("Article modifié avec succès !");
        } catch (NumberFormatException e) {
            afficherErreur("Référence et stock minimal doivent être des nombres !");
        }
    }

    @FXML
    private void supprimer() {
        Article selectionne = tableArticles.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un article !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Supprimer l'article " + selectionne.getReference() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.supprimerArticle(selectionne.getId());
                rafraichirTableau();
                viderFormulaire();
                verifierAlertes();
            }
        });
    }

    @FXML
    private void rechercher() {
        String critere = tfRecherche.getText().trim();
        if (critere.isEmpty()) {
            rafraichirTableau();
            return;
        }
        // AMELIORATION : recherche par référence OU par nom
        List<Article> resultats = dataStore.rechercherArticles(critere);
        observableArticles.setAll(resultats);
        tableArticles.setItems(observableArticles);
    }

    @FXML
    private void afficherTout() {
        tfRecherche.clear();
        rafraichirTableau();
    }

    @FXML
    private void viderFormulaire() {
        tfReference.clear();
        tfNom.clear();
        cbType.setValue(null);
        dpDatePeremption.setValue(null);
        tfStockMinimal.clear();
        rbNon.setSelected(true);
        tableArticles.getSelectionModel().clearSelection();
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void remplirFormulaire(Article a) {
        if (a == null) return;
        tfReference.setText(String.valueOf(a.getReference()));
        tfNom.setText(a.getNom() != null ? a.getNom() : "");
        cbType.setValue(a.getType());
        dpDatePeremption.setValue(a.getDatePeremption());
        tfStockMinimal.setText(String.valueOf(a.getStockMinimal()));
        if (a.isCritique()) rbOui.setSelected(true);
        else rbNon.setSelected(true);
    }

    private void rafraichirTableau() {
        observableArticles.setAll(dataStore.getArticles());
        tableArticles.setItems(observableArticles);
    }

    private void verifierAlertes() {
        List<Article> alertes = dataStore.getArticlesEnAlerte();
        if (!alertes.isEmpty()) {
            lblAlerte.setText("⚠ " + alertes.size() + " article(s) en alerte de stock !");
            lblAlerte.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblAlerte.setText("✓ Stock OK");
            lblAlerte.setStyle("-fx-text-fill: green;");
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
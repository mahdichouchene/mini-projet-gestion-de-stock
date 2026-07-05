package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.Fournisseur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FournisseurController implements Initializable {

    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, Integer> colId;
    @FXML private TableColumn<Fournisseur, String> colNom;
    @FXML private TableColumn<Fournisseur, String> colTelephone;
    @FXML private TableColumn<Fournisseur, String> colEmail;
    @FXML private TableColumn<Fournisseur, String> colAdresse;

    @FXML private TextField tfNom;
    @FXML private TextField tfTelephone;
    @FXML private TextField tfEmail;
    @FXML private TextField tfAdresse;
    @FXML private TextField tfRecherche;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<Fournisseur> observableFournisseurs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        tableFournisseurs.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> remplirFormulaire(newValue)
        );

        rafraichirTableau();
    }

    @FXML
    private void ajouter() {
        if (tfNom.getText().isEmpty()) {
            afficherErreur("Le nom est obligatoire !");
            return;
        }
        Fournisseur f = new Fournisseur(
                tfNom.getText(),
                tfTelephone.getText(),
                tfEmail.getText(),
                tfAdresse.getText()
        );
        dataStore.ajouterFournisseur(f);
        rafraichirTableau();
        viderFormulaire();
        afficherInfo("Fournisseur ajouté avec succès !");
    }

    @FXML
    private void modifier() {
        Fournisseur selectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un fournisseur !");
            return;
        }
        selectionne.setNom(tfNom.getText());
        selectionne.setTelephone(tfTelephone.getText());
        selectionne.setEmail(tfEmail.getText());
        selectionne.setAdresse(tfAdresse.getText());

        dataStore.modifierFournisseur(selectionne);
        rafraichirTableau();
        viderFormulaire();
        afficherInfo("Fournisseur modifié avec succès !");
    }

    @FXML
    private void supprimer() {
        Fournisseur selectionne = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un fournisseur !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Supprimer le fournisseur " + selectionne.getNom() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.supprimerFournisseur(selectionne.getId());
                rafraichirTableau();
                viderFormulaire();
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
        List<Fournisseur> resultats = dataStore.rechercherFournisseurs(critere);
        observableFournisseurs.setAll(resultats);
        tableFournisseurs.setItems(observableFournisseurs);
    }

    @FXML
    private void afficherTout() {
        tfRecherche.clear();
        rafraichirTableau();
    }

    @FXML
    private void viderFormulaire() {
        tfNom.clear();
        tfTelephone.clear();
        tfEmail.clear();
        tfAdresse.clear();
        tableFournisseurs.getSelectionModel().clearSelection();
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void remplirFormulaire(Fournisseur f) {
        if (f == null) return;
        tfNom.setText(f.getNom());
        tfTelephone.setText(f.getTelephone());
        tfEmail.setText(f.getEmail());
        tfAdresse.setText(f.getAdresse());
    }

    private void rafraichirTableau() {
        observableFournisseurs.setAll(dataStore.getFournisseurs());
        tableFournisseurs.setItems(observableFournisseurs);
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
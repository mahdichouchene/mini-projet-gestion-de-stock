package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.Local;
import com.example.demogestionstockisimm.model.NomLocal;
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

public class LocalController implements Initializable {

    @FXML private TableView<Local> tableLocaux;
    @FXML private TableColumn<Local, Integer> colId;
    @FXML private TableColumn<Local, NomLocal> colNom;

    @FXML private ComboBox<NomLocal> cbNom;
    @FXML private TextField tfRecherche;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<Local> observableLocaux = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        cbNom.setItems(FXCollections.observableArrayList(NomLocal.values()));

        tableLocaux.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> remplirFormulaire(newValue)
        );

        rafraichirTableau();
    }

    @FXML
    private void ajouter() {
        if (cbNom.getValue() == null) {
            afficherErreur("Veuillez sélectionner un nom de local !");
            return;
        }
        Local l = new Local(cbNom.getValue());
        dataStore.ajouterLocal(l);
        rafraichirTableau();
        viderFormulaire();
        afficherInfo("Local ajouté avec succès !");
    }

    @FXML
    private void modifier() {
        Local selectionne = tableLocaux.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un local !");
            return;
        }
        if (cbNom.getValue() == null) {
            afficherErreur("Veuillez sélectionner un nom !");
            return;
        }
        selectionne.setNom(cbNom.getValue());
        dataStore.modifierLocal(selectionne);
        rafraichirTableau();
        viderFormulaire();
        afficherInfo("Local modifié avec succès !");
    }

    @FXML
    private void supprimer() {
        Local selectionne = tableLocaux.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un local !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Supprimer le local " + selectionne.getNom().getLabel() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.supprimerLocal(selectionne.getId());
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
        List<Local> resultats = dataStore.rechercherLocaux(critere);
        observableLocaux.setAll(resultats);
        tableLocaux.setItems(observableLocaux);
    }

    @FXML
    private void afficherTout() {
        tfRecherche.clear();
        rafraichirTableau();
    }

    @FXML
    private void viderFormulaire() {
        cbNom.setValue(null);
        tableLocaux.getSelectionModel().clearSelection();
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void remplirFormulaire(Local l) {
        if (l == null) return;
        cbNom.setValue(l.getNom());
    }

    private void rafraichirTableau() {
        observableLocaux.setAll(dataStore.getLocaux());
        tableLocaux.setItems(observableLocaux);
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
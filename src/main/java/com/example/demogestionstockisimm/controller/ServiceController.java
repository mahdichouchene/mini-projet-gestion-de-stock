package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.NomService;
import com.example.demogestionstockisimm.model.Service;
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

public class ServiceController implements Initializable {

    @FXML private TableView<Service> tableServices;
    @FXML private TableColumn<Service, Integer> colId;
    @FXML private TableColumn<Service, NomService> colNom;

    @FXML private ComboBox<NomService> cbNom;
    @FXML private TextField tfRecherche;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<Service> observableServices = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        cbNom.setItems(FXCollections.observableArrayList(NomService.values()));

        tableServices.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> remplirFormulaire(newValue)
        );

        rafraichirTableau();
    }

    @FXML
    private void ajouter() {
        if (cbNom.getValue() == null) {
            afficherErreur("Veuillez sélectionner un nom de service !");
            return;
        }
        Service s = new Service(cbNom.getValue());
        dataStore.ajouterService(s);
        rafraichirTableau();
        viderFormulaire();
        afficherInfo("Service ajouté avec succès !");
    }

    @FXML
    private void modifier() {
        Service selectionne = tableServices.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un service !");
            return;
        }
        if (cbNom.getValue() == null) {
            afficherErreur("Veuillez sélectionner un nom !");
            return;
        }
        selectionne.setNom(cbNom.getValue());
        dataStore.modifierService(selectionne);
        rafraichirTableau();
        viderFormulaire();
        afficherInfo("Service modifié avec succès !");
    }

    @FXML
    private void supprimer() {
        Service selectionne = tableServices.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherErreur("Veuillez sélectionner un service !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Supprimer le service " + selectionne.getNom().getLabel() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.supprimerService(selectionne.getId());
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
        List<Service> resultats = dataStore.rechercherServices(critere);
        observableServices.setAll(resultats);
        tableServices.setItems(observableServices);
    }

    @FXML
    private void afficherTout() {
        tfRecherche.clear();
        rafraichirTableau();
    }

    @FXML
    private void viderFormulaire() {
        cbNom.setValue(null);
        tableServices.getSelectionModel().clearSelection();
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void remplirFormulaire(Service s) {
        if (s == null) return;
        cbNom.setValue(s.getNom());
    }

    private void rafraichirTableau() {
        observableServices.setAll(dataStore.getServices());
        tableServices.setItems(observableServices);
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
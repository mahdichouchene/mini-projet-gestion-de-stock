package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.dao.UtilisateurDAO;
import com.example.demogestionstockisimm.datastore.SessionManager;
import com.example.demogestionstockisimm.model.RoleUtilisateur;
import com.example.demogestionstockisimm.model.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GestionUtilisateursController implements Initializable {

    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, Integer> colId;
    @FXML private TableColumn<Utilisateur, String> colLogin;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colMotDePasse;
    @FXML private TableColumn<Utilisateur, String> colRole;

    @FXML private CheckBox chkVoirMdp;
    @FXML private TextField tfLogin;
    @FXML private PasswordField pfMotDePasse;
    @FXML private Label lblInfo;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private ObservableList<Utilisateur> observableUtilisateurs = FXCollections.observableArrayList();
    private boolean mdpVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colRole.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRole().getLabel())
        );

        // Mots de passe masqués par défaut
        colMotDePasse.setCellValueFactory(data ->
                new SimpleStringProperty("••••••••")
        );

        rafraichirTableau();
    }

    // Toggle affichage mots de passe
    @FXML
    private void toggleMotsDePasse() {
        mdpVisible = chkVoirMdp.isSelected();

        if (mdpVisible) {
            // Afficher les vrais mots de passe
            colMotDePasse.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getMotDePasse())
            );
        } else {
            // Masquer avec des points
            colMotDePasse.setCellValueFactory(data ->
                    new SimpleStringProperty("••••••••")
            );
        }

        // Forcer le rafraîchissement du tableau
        tableUtilisateurs.refresh();
    }

    @FXML
    private void ajouter() {
        String login = tfLogin.getText().trim();
        String motDePasse = pfMotDePasse.getText().trim();

        if (login.isEmpty() || motDePasse.isEmpty()) {
            afficherInfo("Login et mot de passe obligatoires !", false);
            return;
        }
        if (login.length() < 3) {
            afficherInfo("Login doit avoir au moins 3 caracteres !", false);
            return;
        }
        if (motDePasse.length() < 4) {
            afficherInfo("Mot de passe doit avoir au moins 4 caracteres !", false);
            return;
        }

        // nom = login automatiquement
        Utilisateur u = new Utilisateur(0, login, motDePasse, login, RoleUtilisateur.UTILISATEUR);
        boolean succes = utilisateurDAO.ajouter(u);

        if (succes) {
            afficherInfo("Utilisateur '" + login + "' ajoute !", true);
            rafraichirTableau();
            vider();
        } else {
            afficherInfo("Login '" + login + "' deja utilise !", false);
        }
    }

    @FXML
    private void supprimer() {
        Utilisateur selectionne = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherInfo("Veuillez selectionner un utilisateur !", false);
            return;
        }
        if (selectionne.isMagasinier()) {
            afficherInfo("Impossible de supprimer le magasinier !", false);
            return;
        }
        if (selectionne.getId() == SessionManager.getInstance().getUtilisateur().getId()) {
            afficherInfo("Impossible de supprimer votre propre compte !", false);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Supprimer l'utilisateur '" + selectionne.getLogin() + "' ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                utilisateurDAO.supprimer(selectionne.getId());
                rafraichirTableau();
                afficherInfo("Utilisateur supprime !", true);
            }
        });
    }

    @FXML
    private void vider() {
        tfLogin.clear();
        pfMotDePasse.clear();
        tableUtilisateurs.getSelectionModel().clearSelection();
        lblInfo.setText("");
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void rafraichirTableau() {
        observableUtilisateurs.setAll(utilisateurDAO.getAll());
        tableUtilisateurs.setItems(observableUtilisateurs);
        // Réappliquer l'état du toggle après rechargement
        toggleMotsDePasse();
    }

    private void afficherInfo(String message, boolean succes) {
        lblInfo.setText(message);
        lblInfo.setStyle(succes ?
                "-fx-text-fill: green; -fx-font-weight: bold;" :
                "-fx-text-fill: red; -fx-font-weight: bold;"
        );
    }
}
package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.dao.UtilisateurDAO;
import com.example.demogestionstockisimm.datastore.SessionManager;
import com.example.demogestionstockisimm.model.RoleUtilisateur;
import com.example.demogestionstockisimm.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginController {

    // Panel connexion
    @FXML private VBox panelConnexion;
    @FXML private VBox panelInscription;
    @FXML private Button btnTabConnexion;
    @FXML private Button btnTabInscription;

    // Champs connexion
    @FXML private TextField tfLogin;
    @FXML private PasswordField pfMotDePasse;
    @FXML private Label lblErreurConnexion;

    // Champs inscription
    @FXML private TextField tfNouveauLogin;
    @FXML private PasswordField pfNouveauMdp;
    @FXML private PasswordField pfConfirmerMdp;
    @FXML private Label lblErreurInscription;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    // Style onglet actif / inactif
    private static final String STYLE_ACTIF   = "-fx-background-color: #2E75B6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0;";
    private static final String STYLE_INACTIF = "-fx-background-color: #d0e4f7; -fx-text-fill: #2E75B6; -fx-font-weight: bold; -fx-background-radius: 0;";

    @FXML
    private void afficherConnexion() {
        panelConnexion.setVisible(true);
        panelConnexion.setManaged(true);
        panelInscription.setVisible(false);
        panelInscription.setManaged(false);
        btnTabConnexion.setStyle(STYLE_ACTIF);
        btnTabInscription.setStyle(STYLE_INACTIF);
        lblErreurConnexion.setText("");
    }

    @FXML
    private void afficherInscription() {
        panelConnexion.setVisible(false);
        panelConnexion.setManaged(false);
        panelInscription.setVisible(true);
        panelInscription.setManaged(true);
        btnTabConnexion.setStyle(STYLE_INACTIF);
        btnTabInscription.setStyle(STYLE_ACTIF);
        lblErreurInscription.setText("");
    }

    @FXML
    private void seConnecter() throws IOException {
        String login = tfLogin.getText().trim();
        String motDePasse = pfMotDePasse.getText().trim();

        if (login.isEmpty() || motDePasse.isEmpty()) {
            lblErreurConnexion.setText("Veuillez remplir tous les champs !");
            return;
        }

        Utilisateur u = utilisateurDAO.authentifier(login, motDePasse);

        if (u == null) {
            lblErreurConnexion.setText("Login ou mot de passe incorrect !");
            pfMotDePasse.clear();
            return;
        }

        SessionManager.getInstance().connecter(u);
        Main.changerScene("MainMenu.fxml");
    }

    @FXML
    private void creerCompte() throws IOException {
        String login = tfNouveauLogin.getText().trim();
        String mdp = pfNouveauMdp.getText().trim();
        String confirm = pfConfirmerMdp.getText().trim();

        // Validations
        if (login.isEmpty() || mdp.isEmpty() || confirm.isEmpty()) {
            afficherErreurInscription("Veuillez remplir tous les champs !", false);
            return;
        }

        if (login.length() < 3) {
            afficherErreurInscription("Login doit avoir au moins 3 caracteres !", false);
            return;
        }

        if (mdp.length() < 4) {
            afficherErreurInscription("Mot de passe doit avoir au moins 4 caracteres !", false);
            return;
        }

        if (!mdp.equals(confirm)) {
            afficherErreurInscription("Les mots de passe ne correspondent pas !", false);
            pfConfirmerMdp.clear();
            return;
        }

        // Créer le compte avec rôle UTILISATEUR — nom = login
        Utilisateur u = new Utilisateur(0, login, mdp, login, RoleUtilisateur.UTILISATEUR);
        boolean succes = utilisateurDAO.ajouter(u);

        if (!succes) {
            afficherErreurInscription("Ce login est deja utilise !", false);
            return;
        }

        // Succès — connecter automatiquement et aller au menu
        afficherErreurInscription("Compte cree avec succes ! Connexion en cours...", true);
        SessionManager.getInstance().connecter(u);

        // Petit délai puis redirection
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(1)
        );
        pause.setOnFinished(e -> {
            try {
                Main.changerScene("MainMenu.fxml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    private void afficherErreurInscription(String message, boolean succes) {
        lblErreurInscription.setText(message);
        lblErreurInscription.setStyle(succes ?
                "-fx-text-fill: green; -fx-font-weight: bold;" :
                "-fx-text-fill: red; -fx-font-weight: bold;"
        );
    }
}
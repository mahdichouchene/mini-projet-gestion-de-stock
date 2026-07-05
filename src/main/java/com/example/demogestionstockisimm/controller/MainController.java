package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblBienvenue;
    @FXML private Label lblRole;
    @FXML private Button btnArticles;
    @FXML private Button btnLocaux;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnServices;
    @FXML private Button btnCommandesExternes;
    @FXML private Button btnStatistiques;
    @FXML private Button btnUtilisateurs;
    @FXML private Button btnCommandesInternes;
    @FXML private Button btnInventaire;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SessionManager session = SessionManager.getInstance();

        if (session.getUtilisateur() != null) {
            lblBienvenue.setText("Bienvenue, " + session.getUtilisateur().getNom());
            lblRole.setText("Role : " + session.getUtilisateur().getRole().getLabel());
        }

        boolean estMagasinier = session.estMagasinier();

        // Modules réservés au magasinier
        setVisible(btnArticles, estMagasinier);
        setVisible(btnLocaux, estMagasinier);
        setVisible(btnFournisseurs, estMagasinier);
        setVisible(btnServices, estMagasinier);
        setVisible(btnCommandesExternes, estMagasinier);
        setVisible(btnStatistiques, estMagasinier);
        setVisible(btnUtilisateurs, estMagasinier);
    }

    private void setVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    @FXML private void ouvrirArticles(ActionEvent e) throws IOException { Main.changerScene("Articles.fxml"); }
    @FXML private void ouvrirLocaux(ActionEvent e) throws IOException { Main.changerScene("Locaux.fxml"); }
    @FXML private void ouvrirFournisseurs(ActionEvent e) throws IOException { Main.changerScene("Fournisseurs.fxml"); }
    @FXML private void ouvrirServices(ActionEvent e) throws IOException { Main.changerScene("Services.fxml"); }
    @FXML private void ouvrirCommandesExternes(ActionEvent e) throws IOException { Main.changerScene("CommandesExternes.fxml"); }
    @FXML private void ouvrirCommandesInternes(ActionEvent e) throws IOException { Main.changerScene("CommandesInternes.fxml"); }
    @FXML private void ouvrirInventaire(ActionEvent e) throws IOException { Main.changerScene("Inventaire.fxml"); }
    @FXML private void ouvrirStatistiques(ActionEvent e) throws IOException { Main.changerScene("Statistiques.fxml"); }
    @FXML private void ouvrirUtilisateurs(ActionEvent e) throws IOException { Main.changerScene("GestionUtilisateurs.fxml"); }

    @FXML
    private void seDeconnecter(ActionEvent e) throws IOException {
        SessionManager.getInstance().deconnecter();
        Main.changerScene("Login.fxml");
    }
}
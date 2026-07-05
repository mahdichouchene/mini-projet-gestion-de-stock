package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class CommandeExterneController implements Initializable {

    @FXML private TableView<CommandeExterne> tableCommandes;
    @FXML private TableColumn<CommandeExterne, Integer> colId;
    @FXML private TableColumn<CommandeExterne, LocalDate> colDate;
    @FXML private TableColumn<CommandeExterne, String> colFournisseur;
    @FXML private TableColumn<CommandeExterne, String> colStatut;

    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Fournisseur> cbFournisseur;
    @FXML private ComboBox<Article> cbArticle;
    @FXML private ComboBox<Local> cbLocal;
    @FXML private TextField tfQuantite;
    @FXML private TextField tfRecherche;
    @FXML private TextArea taTrace;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<CommandeExterne> observableCommandes = FXCollections.observableArrayList();
    private CommandeExterne commandeEnCours = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colFournisseur.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFournisseur() != null ?
                                data.getValue().getFournisseur().getNom() : ""
                )
        );

        rafraichirComboBox();

        tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        commandeEnCours = newValue;
                        tracer("Commande #" + newValue.getId() + " selectionnee — " +
                                newValue.getLignes().size() + " ligne(s)");
                    }
                }
        );

        dpDate.setValue(LocalDate.now());
        rafraichirTableau();
    }

    private void rafraichirComboBox() {
        cbFournisseur.setItems(FXCollections.observableArrayList(dataStore.getFournisseurs()));
        cbArticle.setItems(FXCollections.observableArrayList(dataStore.getArticles()));
        cbLocal.setItems(FXCollections.observableArrayList(dataStore.getLocaux()));
    }

    @FXML
    private void creer() {
        if (dpDate.getValue() == null || cbFournisseur.getValue() == null) {
            afficherErreur("Veuillez remplir la date et le fournisseur !");
            return;
        }
        CommandeExterne c = new CommandeExterne(dpDate.getValue(), cbFournisseur.getValue());
        dataStore.ajouterCommandeExterne(c);
        commandeEnCours = c;
        rafraichirTableau();
        // Sélectionner automatiquement la commande créée
        for (CommandeExterne cmd : tableCommandes.getItems()) {
            if (cmd.getId() == c.getId()) {
                tableCommandes.getSelectionModel().select(cmd);
                break;
            }
        }
        tracer("Commande #" + c.getId() + " creee — Fournisseur : " + c.getFournisseur().getNom());
        afficherInfo("Commande creee ! Selectionnez-la dans le tableau puis ajoutez les lignes.");
    }

    @FXML
    private void ajouterLigne() {
        if (commandeEnCours == null) {
            afficherErreur("Veuillez d'abord creer une commande !");
            return;
        }
        if (commandeEnCours.getStatut().equals("VALIDEE") ||
                commandeEnCours.getStatut().equals("ANNULEE")) {
            afficherErreur("Impossible de modifier une commande " + commandeEnCours.getStatut());
            return;
        }
        if (cbArticle.getValue() == null || tfQuantite.getText().isEmpty() || cbLocal.getValue() == null) {
            afficherErreur("Veuillez selectionner un article, une quantite et un local !");
            return;
        }
        try {
            int quantite = Integer.parseInt(tfQuantite.getText());
            if (quantite <= 0) {
                afficherErreur("La quantite doit etre superieure a 0 !");
                return;
            }
            // CORRECTION : Constructeur avec 3 paramètres (quantite, article, local)
            LigneCommande ligne = new LigneCommande(quantite, cbArticle.getValue(), cbLocal.getValue());
            dataStore.ajouterLigneCommandeExterne(commandeEnCours.getId(), ligne);
            commandeEnCours.ajouterLigne(ligne);

            tracer("Ligne ajoutee — " + cbArticle.getValue().toString()
                    + " | Qte : " + quantite
                    + " | Local : " + cbLocal.getValue().getNom().getLabel());

            tfQuantite.clear();
            cbArticle.setValue(null);
            cbLocal.setValue(null);
        } catch (NumberFormatException e) {
            afficherErreur("La quantite doit etre un nombre !");
        }
    }

    @FXML
    private void valider() {
        if (commandeEnCours == null) {
            afficherErreur("Veuillez selectionner une commande !");
            return;
        }
        if (commandeEnCours.getStatut().equals("VALIDEE")) {
            afficherErreur("Cette commande est deja validee !");
            return;
        }
        if (commandeEnCours.getStatut().equals("ANNULEE")) {
            afficherErreur("Impossible de valider une commande annulee !");
            return;
        }
        if (commandeEnCours.getLignes().isEmpty()) {
            afficherErreur("La commande ne contient aucune ligne !");
            return;
        }

        // Plus besoin de vérifier cbLocal - chaque ligne a son propre local
        dataStore.validerCommandeExterne(commandeEnCours.getId());

        rafraichirTableau();

        tracer("Commande #" + commandeEnCours.getId() + " VALIDEE");
        afficherInfo("Commande validee ! Le stock a ete mis a jour.");

        commandeEnCours = null;
    }

    @FXML
    private void annuler() {
        if (commandeEnCours == null) {
            afficherErreur("Veuillez selectionner une commande !");
            return;
        }
        if (commandeEnCours.getStatut().equals("VALIDEE")) {
            afficherErreur("Impossible d'annuler une commande deja validee !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Annuler la commande #" + commandeEnCours.getId() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.annulerCommandeExterne(commandeEnCours.getId());
                rafraichirTableau();
                tracer("Commande #" + commandeEnCours.getId() + " ANNULEE");
                commandeEnCours = null;
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
        List<CommandeExterne> resultats = dataStore.rechercherCommandesExternes(critere);
        observableCommandes.setAll(resultats);
        tableCommandes.setItems(observableCommandes);
    }

    @FXML
    private void afficherTout() {
        tfRecherche.clear();
        rafraichirTableau();
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void rafraichirTableau() {
        observableCommandes.setAll(dataStore.getCommandesExternes());
        tableCommandes.setItems(observableCommandes);
    }

    private void tracer(String action) {
        String heure = LocalTime.now().toString().substring(0, 8);
        taTrace.appendText("[" + heure + "] " + action + "\n");
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succes");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
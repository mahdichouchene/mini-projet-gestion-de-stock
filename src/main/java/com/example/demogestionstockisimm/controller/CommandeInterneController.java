package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.model.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class CommandeInterneController implements Initializable {

    @FXML private TableView<CommandeInterne> tableCommandes;
    @FXML private TableColumn<CommandeInterne, Integer> colId;
    @FXML private TableColumn<CommandeInterne, LocalDate> colDate;
    @FXML private TableColumn<CommandeInterne, String> colService;
    @FXML private TableColumn<CommandeInterne, String> colStatut;
    @FXML private TableColumn<CommandeInterne, Integer> colNbLignes;

    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Service> cbService;
    @FXML private ComboBox<Article> cbArticle;
    @FXML private ComboBox<Local> cbLocalLigne; // FIX : local par ligne
    @FXML private TextField tfQuantite;
    @FXML private TextField tfRecherche;
    @FXML private TextArea taTrace;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<CommandeInterne> observableCommandes = FXCollections.observableArrayList();
    private CommandeInterne commandeEnCours = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colService.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getService() != null ?
                                data.getValue().getService().getNom().getLabel() : ""
                )
        );

        colNbLignes.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getLignes().size()).asObject()
        );

        cbService.setItems(FXCollections.observableArrayList(dataStore.getServices()));
        cbArticle.setItems(FXCollections.observableArrayList(dataStore.getArticles()));
        cbLocalLigne.setItems(FXCollections.observableArrayList(dataStore.getLocaux()));

        tableCommandes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        commandeEnCours = newValue;
                        tracer("Commande #" + newValue.getId() + " selectionnee ("
                                + newValue.getLignes().size() + " ligne(s))");
                    }
                }
        );

        dpDate.setValue(LocalDate.now());
        rafraichirTableau();
    }

    @FXML
    private void creer() {
        if (dpDate.getValue() == null || cbService.getValue() == null) {
            afficherErreur("Veuillez remplir la date et le service !");
            return;
        }
        CommandeInterne c = new CommandeInterne(dpDate.getValue(), cbService.getValue());
        dataStore.ajouterCommandeInterne(c);
        commandeEnCours = c;
        rafraichirTableau();
        for (CommandeInterne cmd : tableCommandes.getItems()) {
            if (cmd.getId() == c.getId()) {
                tableCommandes.getSelectionModel().select(cmd);
                break;
            }
        }
        tracer("Commande #" + c.getId() + " creee — " + c.getService().getNom().getLabel());
        afficherInfo("Commande creee ! Ajoutez les lignes avec un local par article.");
    }

    @FXML
    private void ajouterLigne() {
        if (commandeEnCours == null) {
            afficherErreur("Veuillez d'abord creer ou selectionner une commande !");
            return;
        }
        if (commandeEnCours.getStatut().equals("VALIDEE") ||
                commandeEnCours.getStatut().equals("ANNULEE")) {
            afficherErreur("Impossible de modifier une commande " + commandeEnCours.getStatut());
            return;
        }
        if (cbArticle.getValue() == null || tfQuantite.getText().isEmpty() || cbLocalLigne.getValue() == null) {
            afficherErreur("Veuillez selectionner un article, une quantite ET un local source !");
            return;
        }
        try {
            int quantite = Integer.parseInt(tfQuantite.getText());
            if (quantite <= 0) {
                afficherErreur("La quantite doit etre superieure a 0 !");
                return;
            }
            // FIX : créer la ligne avec son local source
            LigneCommande ligne = new LigneCommande(quantite, cbArticle.getValue(), cbLocalLigne.getValue());
            dataStore.ajouterLigneCommandeInterne(commandeEnCours.getId(), ligne);
            commandeEnCours.ajouterLigne(ligne);
            tracer("Ligne : " + cbArticle.getValue().toString()
                    + " x" + quantite
                    + " depuis " + cbLocalLigne.getValue().getNom().getLabel());
            tfQuantite.clear();
            cbArticle.setValue(null);
            cbLocalLigne.setValue(null);
            rafraichirTableau();
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

        // FIX : valider sans passer de local global — chaque ligne a son local
        boolean succes = dataStore.validerCommandeInterne(commandeEnCours.getId());

        if (succes) {
            rafraichirTableau();
            tracer("Commande #" + commandeEnCours.getId() + " VALIDEE — stock decremente par local");
            afficherInfo("Commande validee ! Le stock a ete mis a jour.");
        } else {
            tracer("Echec validation #" + commandeEnCours.getId() + " — Stock insuffisant !");
            afficherErreur("Stock insuffisant pour une ou plusieurs lignes !\nVerifiez l'inventaire.");
        }
    }

    @FXML
    private void annuler() {
        if (commandeEnCours == null) {
            afficherErreur("Veuillez selectionner une commande !");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setContentText("Annuler la commande #" + commandeEnCours.getId() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataStore.annulerCommandeInterne(commandeEnCours.getId());
                rafraichirTableau();
                tracer("Commande #" + commandeEnCours.getId() + " ANNULEE");
            }
        });
    }

    @FXML
    private void rechercher() {
        String critere = tfRecherche.getText().trim();
        if (critere.isEmpty()) { rafraichirTableau(); return; }
        List<CommandeInterne> resultats = dataStore.rechercherCommandesInternes(critere);
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
        observableCommandes.setAll(dataStore.getCommandesInternes());
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
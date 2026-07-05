package com.example.demogestionstockisimm.controller;

import com.example.demogestionstockisimm.Main;
import com.example.demogestionstockisimm.datastore.DataStore;
import com.example.demogestionstockisimm.datastore.SessionManager;
import com.example.demogestionstockisimm.model.*;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class InventaireController implements Initializable {

    @FXML private TableView<Stock> tableStocks;
    @FXML private TableColumn<Stock, Integer> colId;
    @FXML private TableColumn<Stock, String> colArticle;
    @FXML private TableColumn<Stock, String> colNom;
    @FXML private TableColumn<Stock, String> colType;
    @FXML private TableColumn<Stock, String> colLocal;
    @FXML private TableColumn<Stock, Integer> colQuantite;
    @FXML private TableColumn<Stock, Integer> colStockMin;
    @FXML private TableColumn<Stock, String> colAlerte;
    @FXML private TableColumn<Stock, String> colJours;

    @FXML private ComboBox<Local> cbLocal;
    @FXML private Label lblTotalArticles;
    @FXML private Label lblTotalQuantite;
    @FXML private Label lblAlertes;

    // CORRECTION : @FXML au lieu de lookup — la Scene n'est pas disponible dans initialize()
    @FXML private Button btnBilan;

    private DataStore dataStore = DataStore.getInstance();
    private ObservableList<Stock> observableStocks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colArticle.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getArticle() != null ?
                                String.valueOf(data.getValue().getArticle().getReference()) : ""
                )
        );

        colNom.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getArticle() != null &&
                                data.getValue().getArticle().getNom() != null ?
                                data.getValue().getArticle().getNom() : ""
                )
        );

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getArticle() != null ?
                                data.getValue().getArticle().getType().getLabel() : ""
                )
        );

        colLocal.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getLocal() != null ?
                                data.getValue().getLocal().getNom().getLabel() : ""
                )
        );

        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        colStockMin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getArticle() != null ?
                                data.getValue().getArticle().getStockMinimal() : 0
                ).asObject()
        );

        colAlerte.setCellValueFactory(data -> {
            Stock s = data.getValue();
            if (s.getArticle() == null) return new SimpleStringProperty("N/A");
            boolean enAlerte = s.getQuantite() < s.getArticle().getStockMinimal();
            return new SimpleStringProperty(enAlerte ? "ALERTE" : "OK");
        });

        colJours.setCellValueFactory(data -> {
            Stock s = data.getValue();
            if (s.getArticle() == null || s.getArticle().getDatePeremption() == null)
                return new SimpleStringProperty("N/A");
            long jours = ChronoUnit.DAYS.between(LocalDate.now(), s.getArticle().getDatePeremption());
            if (jours < 0) return new SimpleStringProperty("PERIME (" + Math.abs(jours) + "j)");
            if (jours <= 30) return new SimpleStringProperty("!" + jours + " jours");
            return new SimpleStringProperty(jours + " jours");
        });

        tableStocks.setRowFactory(tv -> new TableRow<Stock>() {
            @Override
            protected void updateItem(Stock item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.getArticle() != null) {
                    boolean alerte = item.getQuantite() < item.getArticle().getStockMinimal();
                    LocalDate peremption = item.getArticle().getDatePeremption();
                    boolean perime = peremption != null && peremption.isBefore(LocalDate.now());
                    boolean proche = peremption != null && !perime &&
                            peremption.isBefore(LocalDate.now().plusDays(30));
                    if (perime) setStyle("-fx-background-color: #ffaaaa;");
                    else if (alerte || proche) setStyle("-fx-background-color: #fff3cd;");
                    else setStyle("");
                } else {
                    setStyle("");
                }
            }
        });

        cbLocal.setItems(FXCollections.observableArrayList(dataStore.getLocaux()));

        // CORRECTION : cacher le bouton bilan pour les non-magasiniers ici (pas dans initialize via lookup)
        boolean estMagasinier = SessionManager.getInstance().estMagasinier();
        btnBilan.setVisible(estMagasinier);
        btnBilan.setManaged(estMagasinier);

        afficherTout();
    }

    @FXML
    private void filtrerParLocal() {
        Local localSelectionne = cbLocal.getValue();
        if (localSelectionne == null) {
            afficherErreur("Veuillez selectionner un local !");
            return;
        }
        List<Stock> filtres = dataStore.filtrer(
                dataStore.getStocks(),
                s -> s.getLocal() != null && s.getLocal().getId() == localSelectionne.getId()
        );
        rafraichirTableau(filtres);
    }

    @FXML
    private void afficherTout() {
        cbLocal.setValue(null);
        rafraichirTableau(dataStore.getStocks());
    }

    @FXML
    private void genererBilan() {
        List<Stock> tousStocks = dataStore.getStocks();
        int totalQuantite = 0;
        for (Stock s : tousStocks) totalQuantite += s.getQuantite();

        List<Stock> alertes = dataStore.filtrer(tousStocks,
                s -> s.getQuantite() < s.getArticle().getStockMinimal());
        List<Article> prochesPeremption = dataStore.getArticlesProchesPeremption();
        List<Article> perimees = dataStore.getArticlesPerimees();
        List<Article> critiques = dataStore.getArticlesCritiques();

        StringBuilder bilan = new StringBuilder();
        bilan.append("=== BILAN ANNUEL DU STOCK ===\n\n");
        bilan.append("Total articles en stock : ").append(tousStocks.size()).append("\n");
        bilan.append("Quantite totale : ").append(totalQuantite).append("\n");
        bilan.append("Articles en alerte : ").append(alertes.size()).append("\n");
        bilan.append("Proches peremption : ").append(prochesPeremption.size()).append("\n");
        bilan.append("Articles perimes : ").append(perimees.size()).append("\n");
        bilan.append("Articles critiques : ").append(critiques.size()).append("\n\n");

        if (!alertes.isEmpty()) {
            bilan.append("=== ALERTES STOCK ===\n");
            for (Stock s : alertes) {
                bilan.append("- Ref.").append(s.getArticle().getReference())
                        .append(" ").append(s.getArticle().getNom() != null ? s.getArticle().getNom() : "")
                        .append(" | Stock:").append(s.getQuantite())
                        .append(" / Min:").append(s.getArticle().getStockMinimal()).append("\n");
            }
            bilan.append("\n");
        }

        if (!prochesPeremption.isEmpty()) {
            bilan.append("=== PROCHES PEREMPTION ===\n");
            for (Article a : prochesPeremption) {
                long jours = ChronoUnit.DAYS.between(LocalDate.now(), a.getDatePeremption());
                bilan.append("- Ref.").append(a.getReference())
                        .append(" | Restant:").append(jours).append(" jours\n");
            }
            bilan.append("\n");
        }

        if (!perimees.isEmpty()) {
            bilan.append("=== ARTICLES PERIMES ===\n");
            for (Article a : perimees) {
                long jours = ChronoUnit.DAYS.between(a.getDatePeremption(), LocalDate.now());
                bilan.append("- Ref.").append(a.getReference())
                        .append(" | Perime depuis:").append(jours).append(" jours\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bilan Annuel");
        alert.setHeaderText("Inventaire complet du magasin ISIMM");
        alert.setContentText(bilan.toString());
        alert.getDialogPane().setPrefWidth(600);
        alert.getDialogPane().setPrefHeight(500);
        alert.showAndWait();
    }

    @FXML
    private void retourMenu() throws IOException {
        Main.changerScene("MainMenu.fxml");
    }

    private void rafraichirTableau(List<Stock> liste) {
        observableStocks.setAll(liste);
        tableStocks.setItems(observableStocks);
        mettreAJourStatistiques(liste);
    }

    private void mettreAJourStatistiques(List<Stock> liste) {
        lblTotalArticles.setText("Total articles : " + liste.size());
        int totalQte = 0;
        for (Stock s : liste) totalQte += s.getQuantite();
        lblTotalQuantite.setText("Quantite totale : " + totalQte);

        List<Stock> alertes = dataStore.filtrer(liste,
                s -> s.getArticle() != null && s.getQuantite() < s.getArticle().getStockMinimal());
        if (!alertes.isEmpty()) {
            lblAlertes.setText(alertes.size() + " article(s) en alerte !");
            lblAlertes.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblAlertes.setText("Aucune alerte");
            lblAlertes.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
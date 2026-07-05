package com.example.demogestionstockisimm.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;


public class CommandeExterne implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private LocalDate dateCommande;
    private String statut;
    private Fournisseur fournisseur;
    private List<LigneCommande> lignes;

    public CommandeExterne() {
        this.lignes = new ArrayList<>();
        this.statut = "EN_ATTENTE";
    }

    public CommandeExterne(LocalDate dateCommande, Fournisseur fournisseur) {
        this();
        this.dateCommande = dateCommande;
        this.fournisseur = fournisseur;
    }

    public CommandeExterne(int id, LocalDate dateCommande, String statut, Fournisseur fournisseur) {
        this();
        this.id = id;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.fournisseur = fournisseur;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    public void ajouterLigne(LigneCommande ligne) {
        this.lignes.add(ligne);
    }

    public void supprimerLigne(LigneCommande ligne) {
        this.lignes.remove(ligne);
    }

    @Override
    public String toString() {
        return "CommandeExterne{" +
                "id=" + id +
                ", dateCommande=" + dateCommande +
                ", statut='" + statut + '\'' +
                ", fournisseur=" + (fournisseur != null ? fournisseur.getNom() : "null") +
                '}';
    }
}


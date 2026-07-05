package com.example.demogestionstockisimm.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeInterne implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private LocalDate dateCommande;
    private String statut;
    private Service service;
    private List<LigneCommande> lignes;

    public CommandeInterne() {
        this.lignes = new ArrayList<>();
        this.statut = "EN_ATTENTE";
    }

    public CommandeInterne(LocalDate dateCommande, Service service) {
        this();
        this.dateCommande = dateCommande;
        this.service = service;
    }

    public CommandeInterne(int id, LocalDate dateCommande, String statut, Service service) {
        this();
        this.id = id;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.service = service;
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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
        return "CommandeInterne{" +
                "id=" + id +
                ", dateCommande=" + dateCommande +
                ", statut='" + statut + '\'' +
                ", service=" + (service != null ? service.getNom() : "null") +
                '}';
    }
}


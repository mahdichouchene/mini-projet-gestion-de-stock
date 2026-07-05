package com.example.demogestionstockisimm.model;

import java.io.Serializable;

public class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private NomService nom;

    public Service() {
    }

    public Service(NomService nom) {
        this.nom = nom;
    }

    public Service(int id, NomService nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NomService getNom() {
        return nom;
    }

    public void setNom(NomService nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return nom != null ? nom.getLabel() : "Inconnu";
    }
}


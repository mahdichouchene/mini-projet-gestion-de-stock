package com.example.demogestionstockisimm.model;

import java.io.Serializable;

public class Local implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private NomLocal nom;

    public Local() {
    }

    public Local(NomLocal nom) {
        this.nom = nom;
    }

    public Local(int id, NomLocal nom) {
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

    public NomLocal getNom() {
        return nom;
    }

    public void setNom(NomLocal nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return nom != null ? nom.getLabel() : "Inconnu";
    }
}


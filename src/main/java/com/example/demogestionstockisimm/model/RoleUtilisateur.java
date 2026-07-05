package com.example.demogestionstockisimm.model;

public enum RoleUtilisateur {
    MAGASINIER("Magasinier"),
    UTILISATEUR("Utilisateur");

    private final String label;

    RoleUtilisateur(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
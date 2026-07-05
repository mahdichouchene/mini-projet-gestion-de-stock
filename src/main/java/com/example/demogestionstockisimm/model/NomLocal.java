package com.example.demogestionstockisimm.model;

public enum NomLocal {
    BIBLIOTHEQUE("Bibliothèque"),
    AMPHIS("Amphithéâtres"),
    SALLES_ENSEIGNEMENT("Salles d'enseignement"),
    ADMINISTRATION("Administration"),
    MAGASIN("Magasin"),
    BUREAUX_ENSEIGNANTS("Bureaux enseignants");

    private final String label;

    NomLocal(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static NomLocal fromLabel(String label) {
        for (NomLocal local : NomLocal.values()) {
            if (local.label.equals(label)) {
                return local;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}


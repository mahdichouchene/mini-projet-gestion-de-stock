package com.example.demogestionstockisimm.model;

public enum NomService {
    SCOLARITE("Scolarité"),
    PHOTOCOPIE("Photocopie"),
    BIBLIOTHEQUE("Bibliothèque");

    private final String label;

    NomService(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static NomService fromLabel(String label) {
        for (NomService service : NomService.values()) {
            if (service.label.equals(label)) {
                return service;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}


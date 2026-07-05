package com.example.demogestionstockisimm.model;

public enum TypeArticle {
    CONSOMMABLE("Consommable"),
    DURABLE("Durable");

    private final String label;

    TypeArticle(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TypeArticle fromLabel(String label) {
        for (TypeArticle type : TypeArticle.values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}


/*

package com.example.demogestionstockisimm.model;

import java.time.LocalDate;
import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int reference;
    private TypeArticle type;
    private LocalDate datePeremption;
    private int stockMinimal;
    private boolean critique;

    public Article() {
    }

    public Article(int reference, TypeArticle type, LocalDate datePeremption, int stockMinimal,  boolean critique) {
        this.reference = reference;
        this.type = type;
        this.datePeremption = datePeremption;
        this.stockMinimal = stockMinimal;
        this.critique = critique;
    }

    public Article(int id, int reference, TypeArticle type, LocalDate datePeremption, int stockMinimal,  boolean critique) {
        this.id = id;
        this.reference = reference;
        this.type = type;
        this.datePeremption = datePeremption;
        this.stockMinimal = stockMinimal;
        this.critique = critique;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public TypeArticle getType() {
        return type;
    }

    public void setType(TypeArticle type) {
        this.type = type;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    public int getStockMinimal() {
        return stockMinimal;
    }

    public void setStockMinimal(int stockMinimal) {
        this.stockMinimal = stockMinimal;
    }

    public boolean isCritique() {
        return critique;
    }

    public void setCritique(boolean critique) {
        this.critique = critique;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", reference=" + reference +
                ", type=" + type +
                ", datePeremption=" + datePeremption +
                ", stockMinimal=" + stockMinimal +
                '}';
    }
}

*/

package com.example.demogestionstockisimm.model;

import java.time.LocalDate;
import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int reference;
    private String nom;
    private TypeArticle type;
    private LocalDate datePeremption;
    private int stockMinimal;
    private boolean critique;

    public Article() {}

    public Article(int reference, String nom, TypeArticle type, LocalDate datePeremption, int stockMinimal, boolean critique) {
        this.reference = reference;
        this.nom = nom;
        this.type = type;
        this.datePeremption = datePeremption;
        this.stockMinimal = stockMinimal;
        this.critique = critique;
    }

    public Article(int id, int reference, String nom, TypeArticle type, LocalDate datePeremption, int stockMinimal, boolean critique) {
        this.id = id;
        this.reference = reference;
        this.nom = nom;
        this.type = type;
        this.datePeremption = datePeremption;
        this.stockMinimal = stockMinimal;
        this.critique = critique;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReference() { return reference; }
    public void setReference(int reference) { this.reference = reference; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public TypeArticle getType() { return type; }
    public void setType(TypeArticle type) { this.type = type; }
    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
    public int getStockMinimal() { return stockMinimal; }
    public void setStockMinimal(int stockMinimal) { this.stockMinimal = stockMinimal; }
    public boolean isCritique() { return critique; }
    public void setCritique(boolean critique) { this.critique = critique; }

    @Override
    public String toString() {
        return "Réf." + reference + (nom != null && !nom.isEmpty() ? " - " + nom : "") + " (" + (type != null ? type.getLabel() : "") + ")";
    }
}
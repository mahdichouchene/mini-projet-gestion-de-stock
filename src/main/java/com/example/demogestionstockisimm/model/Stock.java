package com.example.demogestionstockisimm.model;

import java.io.Serializable;

public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private Article article;
    private Local local;
    private int quantite;

    public Stock() {
    }

    public Stock(Article article, Local local, int quantite) {
        this.article = article;
        this.local = local;
        this.quantite = quantite;
    }

    public Stock(int id, Article article, Local local, int quantite) {
        this.id = id;
        this.article = article;
        this.local = local;
        this.quantite = quantite;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", article=" + (article != null ? article.getReference() : "null") +
                ", local=" + (local != null ? local.getNom() : "null") +
                ", quantite=" + quantite +
                '}';
    }
}


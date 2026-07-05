package com.example.demogestionstockisimm.model;

import java.io.Serializable;

public class LigneCommande implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int quantite;
    private Article article;
    private Local local;

    public LigneCommande() {
    }

    // Constructeur pour commande interne (sans local) - création
    public LigneCommande(int quantite, Article article) {
        this.quantite = quantite;
        this.article = article;
        this.local = null;
    }

    // Constructeur pour commande externe (avec local) - création
    public LigneCommande(int quantite, Article article, Local local) {
        this.quantite = quantite;
        this.article = article;
        this.local = local;
    }

    // Constructeur avec id pour commande interne (chargement depuis BD)
    public LigneCommande(int id, int quantite, Article article) {
        this.id = id;
        this.quantite = quantite;
        this.article = article;
        this.local = null;
    }

    // Constructeur avec id pour commande externe (chargement depuis BD)
    public LigneCommande(int id, int quantite, Article article, Local local) {
        this.id = id;
        this.quantite = quantite;
        this.article = article;
        this.local = local;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }
    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "id=" + id +
                ", quantite=" + quantite +
                ", article=" + (article != null ? article.getReference() : "null") +
                ", local=" + (local != null ? local.getNom() : "null") +
                '}';
    }
}
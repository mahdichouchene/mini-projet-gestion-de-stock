package com.example.demogestionstockisimm.model;

import java.io.Serializable;

public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String login;
    private String motDePasse;
    private String nom;
    private RoleUtilisateur role;

    public Utilisateur() {}

    public Utilisateur(int id, String login, String motDePasse, String nom, RoleUtilisateur role) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public RoleUtilisateur getRole() { return role; }
    public void setRole(RoleUtilisateur role) { this.role = role; }

    public boolean isMagasinier() {
        return role == RoleUtilisateur.MAGASINIER;
    }

    @Override
    public String toString() {
        return nom + " (" + role.getLabel() + ")";
    }
}
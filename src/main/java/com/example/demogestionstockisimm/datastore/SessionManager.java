package com.example.demogestionstockisimm.datastore;

import com.example.demogestionstockisimm.model.Utilisateur;

// Singleton qui garde en mémoire l'utilisateur connecté
public class SessionManager {
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void connecter(Utilisateur u) {
        this.utilisateurConnecte = u;
        System.out.println("OK Session ouverte: " + u.getLogin() + " (" + u.getRole().getLabel() + ")");
    }

    public void deconnecter() {
        System.out.println("OK Session fermée: " + (utilisateurConnecte != null ? utilisateurConnecte.getLogin() : ""));
        this.utilisateurConnecte = null;
    }

    public Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    public boolean estMagasinier() {
        return utilisateurConnecte != null && utilisateurConnecte.isMagasinier();
    }
}
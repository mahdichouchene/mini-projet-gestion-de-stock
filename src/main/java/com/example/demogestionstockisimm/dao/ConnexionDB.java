package com.example.demogestionstockisimm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {

    private static ConnexionDB instance;
    private Connection connection;

    private static final String URL = "jdbc:postgresql://localhost:5432/gestionstock";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root"; // ton mot de passe

    private ConnexionDB() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            this.connection.setAutoCommit(true);
            System.out.println("✓ Connexion PostgreSQL réussie !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver PostgreSQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    public static ConnexionDB getInstance() {
        if (instance == null) {
            instance = new ConnexionDB();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de reconnexion : " + e.getMessage());
        }
        return connection;
    }

    public void fermerConnexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture : " + e.getMessage());
        }
    }
}
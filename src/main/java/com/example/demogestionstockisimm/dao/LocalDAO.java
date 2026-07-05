package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.Local;
import com.example.demogestionstockisimm.model.NomLocal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalDAO {
    private Connection connection;

    public LocalDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
    }

    public void ajouter(Local local) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "INSERT INTO local_stockage (nom) VALUES (?::nom_local)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, local.getNom().name().toLowerCase());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    local.setId(rs.getInt(1));
                }
            }
            System.out.println("✓ Local ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un local: " + e.getMessage());
        }
    }

    public void modifier(Local local) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "UPDATE local_stockage SET nom = ?::nom_local WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, local.getNom().name().toLowerCase());
            stmt.setInt(2, local.getId());
            stmt.executeUpdate();
            System.out.println("✓ Local modifié avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification d'un local: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "DELETE FROM local_stockage WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✓ Local supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression d'un local: " + e.getMessage());
        }
    }

    public List<Local> getAll() {
        List<Local> locaux = new ArrayList<>();
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return locaux;
        }
        String sql = "SELECT id, nom FROM local_stockage ORDER BY nom";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Local local = new Local(
                        rs.getInt("id"),
                        NomLocal.valueOf(rs.getString("nom").toUpperCase())
                );
                locaux.add(local);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des locaux: " + e.getMessage());
        }
        return locaux;
    }

    public List<Local> rechercher(String critere) {
        List<Local> locaux = new ArrayList<>();
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return locaux;
        }
        String sql = "SELECT id, nom FROM local_stockage WHERE nom::text LIKE ? ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Local local = new Local(
                            rs.getInt("id"),
                            NomLocal.valueOf(rs.getString("nom").toUpperCase())
                    );
                    locaux.add(local);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de locaux: " + e.getMessage());
        }
        return locaux;
    }

    public Local getById(int id) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return null;
        }
        String sql = "SELECT id, nom FROM local_stockage WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Local(
                            rs.getInt("id"),
                            NomLocal.valueOf(rs.getString("nom").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération d'un local: " + e.getMessage());
        }
        return null;
    }
}
package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.Fournisseur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {
    private Connection connection;

    public FournisseurDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
    }

    public void ajouter(Fournisseur fournisseur) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "INSERT INTO fournisseur (nom, telephone, email, adresse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getTelephone());
            stmt.setString(3, fournisseur.getEmail());
            stmt.setString(4, fournisseur.getAdresse());
            
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fournisseur.setId(rs.getInt(1));
                }
            }
            System.out.println("✓ Fournisseur ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un fournisseur: " + e.getMessage());
        }
    }

    public void modifier(Fournisseur fournisseur) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "UPDATE fournisseur SET nom = ?, telephone = ?, email = ?, adresse = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getTelephone());
            stmt.setString(3, fournisseur.getEmail());
            stmt.setString(4, fournisseur.getAdresse());
            stmt.setInt(5, fournisseur.getId());
            
            stmt.executeUpdate();
            System.out.println("✓ Fournisseur modifié avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification d'un fournisseur: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "DELETE FROM fournisseur WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✓ Fournisseur supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression d'un fournisseur: " + e.getMessage());
        }
    }

    public List<Fournisseur> getAll() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return fournisseurs;
        }
        String sql = "SELECT id, nom, telephone, email, adresse FROM fournisseur ORDER BY nom";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("telephone"),
                    rs.getString("email"),
                    rs.getString("adresse")
                );
                fournisseurs.add(fournisseur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fournisseurs: " + e.getMessage());
        }
        return fournisseurs;
    }

    public List<Fournisseur> rechercher(String critere) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return fournisseurs;
        }
        String sql = "SELECT id, nom, telephone, email, adresse FROM fournisseur WHERE nom LIKE ? ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fournisseur fournisseur = new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("adresse")
                    );
                    fournisseurs.add(fournisseur);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de fournisseurs: " + e.getMessage());
        }
        return fournisseurs;
    }

    public Fournisseur getById(int id) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return null;
        }
        String sql = "SELECT id, nom, telephone, email, adresse FROM fournisseur WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("adresse")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération d'un fournisseur: " + e.getMessage());
        }
        return null;
    }
}


package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.RoleUtilisateur;
import com.example.demogestionstockisimm.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    private Connection connection;

    public UtilisateurDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
    }

    public Utilisateur authentifier(String login, String motDePasse) {
        if (connection == null) return null;
        String sql = "SELECT id, login, mot_de_passe, nom, role FROM utilisateur WHERE login = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, motDePasse);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("mot_de_passe"),
                            rs.getString("nom"),
                            RoleUtilisateur.valueOf(rs.getString("role").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur authentification: " + e.getMessage());
        }
        return null;
    }

    public List<Utilisateur> getAll() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        if (connection == null) return utilisateurs;
        String sql = "SELECT id, login, mot_de_passe, nom, role FROM utilisateur ORDER BY role, login";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("mot_de_passe"),
                        rs.getString("nom"),
                        RoleUtilisateur.valueOf(rs.getString("role").toUpperCase())
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAll utilisateurs: " + e.getMessage());
        }
        return utilisateurs;
    }

    // Vérifier si le login existe déjà
    public boolean existeLogin(String login) {
        if (connection == null) return false;
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur existeLogin: " + e.getMessage());
        }
        return false;
    }

    // Ajouter un utilisateur — nom = login automatiquement
    public boolean ajouter(Utilisateur u) {
        if (connection == null) return false;

        // Vérifier doublon login
        if (existeLogin(u.getLogin())) {
            System.err.println("Login deja existant: " + u.getLogin());
            return false;
        }

        // nom = login si nom non fourni
        if (u.getNom() == null || u.getNom().isEmpty()) {
            u.setNom(u.getLogin());
        }

        String sql = "INSERT INTO utilisateur (login, mot_de_passe, nom, role) VALUES (?, ?, ?, ?::role_utilisateur)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getMotDePasse());
            stmt.setString(3, u.getNom());
            stmt.setString(4, u.getRole().name().toLowerCase());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
            System.out.println("OK Utilisateur ajoute: " + u.getLogin());
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur ajout utilisateur: " + e.getMessage());
            return false;
        }
    }

    public void supprimer(int id) {
        if (connection == null) return;
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("OK Utilisateur supprime id=" + id);
        } catch (SQLException e) {
            System.err.println("Erreur supprimer utilisateur: " + e.getMessage());
        }
    }
}
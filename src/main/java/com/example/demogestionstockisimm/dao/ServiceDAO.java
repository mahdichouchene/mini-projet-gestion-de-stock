package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.Service;
import com.example.demogestionstockisimm.model.NomService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    private Connection connection;

    public ServiceDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
    }

    public void ajouter(Service service) {
        if (connection == null) return;
        String sql = "INSERT INTO service (nom) VALUES (?::nom_service)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, service.getNom().name().toLowerCase());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) service.setId(rs.getInt(1));
            }
            System.out.println("✓ Service ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un service: " + e.getMessage());
        }
    }

    public void modifier(Service service) {
        if (connection == null) return;
        String sql = "UPDATE service SET nom = ?::nom_service WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, service.getNom().name().toLowerCase());
            stmt.setInt(2, service.getId());
            stmt.executeUpdate();
            System.out.println("✓ Service modifié avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification d'un service: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) return;
        String sql = "DELETE FROM service WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✓ Service supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression d'un service: " + e.getMessage());
        }
    }

    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        if (connection == null) return services;
        String sql = "SELECT id, nom FROM service ORDER BY nom";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Service service = new Service(
                    rs.getInt("id"),
                    NomService.valueOf(rs.getString("nom").toUpperCase())
                );
                services.add(service);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des services: " + e.getMessage());
        }
        return services;
    }

    public List<Service> rechercher(String critere) {
        List<Service> services = new ArrayList<>();
        if (connection == null) return services;
        String sql = "SELECT id, nom FROM service WHERE nom::text LIKE ? ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere.toUpperCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service(
                        rs.getInt("id"),
                        NomService.valueOf(rs.getString("nom").toUpperCase())
                    );
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de services: " + e.getMessage());
        }
        return services;
    }

    public Service getById(int id) {
        if (connection == null) return null;
        String sql = "SELECT id, nom FROM service WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Service(
                        rs.getInt("id"),
                        NomService.valueOf(rs.getString("nom").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération d'un service: " + e.getMessage());
        }
        return null;
    }
}


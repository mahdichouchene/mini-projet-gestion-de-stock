package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {
    private Connection connection;
    private ArticleDAO articleDAO;
    private LocalDAO localDAO;

    public StockDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
        this.articleDAO = new ArticleDAO();
        this.localDAO = new LocalDAO();
    }

    public void ajouter(Stock stock) {
        if (connection == null) return;
        // Changement : vérifie d'abord si le stock existe déjà pour éviter les doublons
        int existant = getQuantiteByArticleAndLocal(stock.getArticle().getId(), stock.getLocal().getId());
        if (existant >= 0) {
            // Stock existe déjà → incrémenter
            incrementerQuantite(stock.getArticle().getId(), stock.getLocal().getId(), stock.getQuantite());
            System.out.println("✓ Stock existant incrémenté");
            return;
        }
        String sql = "INSERT INTO stock (article_id, local_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, stock.getArticle().getId());
            stmt.setInt(2, stock.getLocal().getId());
            stmt.setInt(3, stock.getQuantite());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) stock.setId(rs.getInt(1));
            }
            System.out.println("✓ Nouveau stock créé article=" + stock.getArticle().getId()
                    + " local=" + stock.getLocal().getId()
                    + " quantite=" + stock.getQuantite());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un stock: " + e.getMessage());
        }
    }

    public void modifier(Stock stock) {
        if (connection == null) return;
        String sql = "UPDATE stock SET article_id = ?, local_id = ?, quantite = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, stock.getArticle().getId());
            stmt.setInt(2, stock.getLocal().getId());
            stmt.setInt(3, stock.getQuantite());
            stmt.setInt(4, stock.getId());
            stmt.executeUpdate();
            System.out.println("✓ Stock modifié avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification d'un stock: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) return;
        String sql = "DELETE FROM stock WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✓ Stock supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression d'un stock: " + e.getMessage());
        }
    }

    public List<Stock> getAll() {
        List<Stock> stocks = new ArrayList<>();
        if (connection == null) return stocks;
        String sql = "SELECT id, article_id, local_id, quantite FROM stock ORDER BY article_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Article article = articleDAO.getById(rs.getInt("article_id"));
                Local local = localDAO.getById(rs.getInt("local_id"));
                if (article != null && local != null) {
                    Stock stock = new Stock(rs.getInt("id"), article, local, rs.getInt("quantite"));
                    stocks.add(stock);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des stocks: " + e.getMessage());
        }
        return stocks;
    }

    public Stock getById(int id) {
        if (connection == null) return null;
        String sql = "SELECT id, article_id, local_id, quantite FROM stock WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Article article = articleDAO.getById(rs.getInt("article_id"));
                    Local local = localDAO.getById(rs.getInt("local_id"));
                    return new Stock(rs.getInt("id"), article, local, rs.getInt("quantite"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération d'un stock: " + e.getMessage());
        }
        return null;
    }

    // Changement : retourne -1 si le stock n'existe PAS (avant retournait 0 = ambiguïté)
    public int getQuantiteByArticleAndLocal(int articleId, int localId) {
        if (connection == null) return -1;
        String sql = "SELECT quantite FROM stock WHERE article_id = ? AND local_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, articleId);
            stmt.setInt(2, localId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("quantite");
            }
        } catch (SQLException e) {
            System.err.println("Erreur getQuantiteByArticleAndLocal: " + e.getMessage());
        }
        return -1; // -1 = stock inexistant dans ce local
    }

    public void incrementerQuantite(int articleId, int localId, int quantite) {
        if (connection == null) return;
        String sql = "UPDATE stock SET quantite = quantite + ? WHERE article_id = ? AND local_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, articleId);
            stmt.setInt(3, localId);
            int rows = stmt.executeUpdate();
            System.out.println("✓ INCREMENT stock article=" + articleId
                    + " local=" + localId + " +=" + quantite + " rows=" + rows);
        } catch (SQLException e) {
            System.err.println("Erreur incrementerQuantite: " + e.getMessage());
        }
    }

    public void decrementerQuantite(int articleId, int localId, int quantite) {
        if (connection == null) return;
        String sql = "UPDATE stock SET quantite = quantite - ? WHERE article_id = ? AND local_id = ? AND quantite >= ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, articleId);
            stmt.setInt(3, localId);
            stmt.setInt(4, quantite);
            int rows = stmt.executeUpdate();
            System.out.println("✓ DECREMENT stock article=" + articleId
                    + " local=" + localId + " -=" + quantite + " rows=" + rows);
        } catch (SQLException e) {
            System.err.println("Erreur decrementerQuantite: " + e.getMessage());
        }
    }

    // Changement : retourne -1 si inexistant (cohérence avec getQuantiteByArticleAndLocal)
    public int getQuantiteByArticle(int articleId) {
        if (connection == null) return -1;
        String sql = "SELECT quantite FROM stock WHERE article_id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, articleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("quantite");
            }
        } catch (SQLException e) {
            System.err.println("Erreur getQuantiteByArticle: " + e.getMessage());
        }
        return -1;
    }

    public void incrementerQuantiteParArticle(int articleId, int quantite) {
        if (connection == null) return;
        String sql = "UPDATE stock SET quantite = quantite + ? WHERE article_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, articleId);
            stmt.executeUpdate();
            System.out.println("✓ Stock incrementé article=" + articleId);
        } catch (SQLException e) {
            System.err.println("Erreur incrementerQuantiteParArticle: " + e.getMessage());
        }
    }
}
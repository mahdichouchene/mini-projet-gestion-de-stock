/*

package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.Article;
import com.example.demogestionstockisimm.model.TypeArticle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {
    private Connection connection;

    public ArticleDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
    }

    public void ajouter(Article article) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "INSERT INTO article (reference, type, date_peremption, stock_minimal, critique) VALUES (?, ?::type_article, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, article.getReference());
            stmt.setString(2, article.getType().name().toLowerCase());
            stmt.setDate(3, java.sql.Date.valueOf(article.getDatePeremption()));
            stmt.setInt(4, article.getStockMinimal());
            stmt.setBoolean(5, article.isCritique());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    article.setId(rs.getInt(1));
                }
            }
            System.out.println("✓ Article ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un article: " + e.getMessage());
        }
    }

    public void modifier(Article article) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "UPDATE article SET reference = ?, type = ?::type_article, date_peremption = ?, stock_minimal = ?, critique = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, article.getReference());
            stmt.setString(2, article.getType().name().toLowerCase());
            stmt.setDate(3, java.sql.Date.valueOf(article.getDatePeremption()));
            stmt.setInt(4, article.getStockMinimal());
            stmt.setBoolean(5, article.isCritique());
            stmt.setInt(6, article.getId());
            stmt.executeUpdate();
            System.out.println("✓ Article modifié avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification d'un article: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return;
        }
        String sql = "DELETE FROM article WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✓ Article supprimé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression d'un article: " + e.getMessage());
        }
    }

    public List<Article> getAll() {
        List<Article> articles = new ArrayList<>();
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return articles;
        }
        String sql = "SELECT id, reference, type, date_peremption, stock_minimal, critique FROM article ORDER BY reference";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Article article = new Article(
                        rs.getInt("id"),
                        rs.getInt("reference"),
                        TypeArticle.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDate("date_peremption").toLocalDate(),
                        rs.getInt("stock_minimal"),
                        rs.getBoolean("critique")
                );
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
        }
        return articles;
    }

    public List<Article> rechercher(String critere) {
        List<Article> articles = new ArrayList<>();
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return articles;
        }
        String sql = "SELECT id, reference, type, date_peremption, stock_minimal, critique FROM article WHERE CAST(reference AS TEXT) LIKE ? ORDER BY reference";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article(
                            rs.getInt("id"),
                            rs.getInt("reference"),
                            TypeArticle.valueOf(rs.getString("type").toUpperCase()),
                            rs.getDate("date_peremption").toLocalDate(),
                            rs.getInt("stock_minimal"),
                            rs.getBoolean("critique")
                    );
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'articles: " + e.getMessage());
        }
        return articles;
    }

    public Article getById(int id) {
        if (connection == null) {
            System.err.println("Erreur: Connexion à la base de données non disponible");
            return null;
        }
        String sql = "SELECT id, reference, type, date_peremption, stock_minimal, critique FROM article WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Article(
                            rs.getInt("id"),
                            rs.getInt("reference"),
                            TypeArticle.valueOf(rs.getString("type").toUpperCase()),
                            rs.getDate("date_peremption").toLocalDate(),
                            rs.getInt("stock_minimal"),
                            rs.getBoolean("critique")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération d'un article: " + e.getMessage());
        }
        return null;
    }
}

*/

package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.Article;
import com.example.demogestionstockisimm.model.TypeArticle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {
    private Connection connection;

    public ArticleDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
    }

    // AMELIORATION 1 : vérifier doublon référence avant d'insérer
    public boolean existeReference(int reference) {
        if (connection == null) return false;
        String sql = "SELECT COUNT(*) FROM article WHERE reference = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reference);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur existeReference: " + e.getMessage());
        }
        return false;
    }

    public void ajouter(Article article) {
        if (connection == null) return;
        if (existeReference(article.getReference())) {
            System.err.println("DOUBLON: référence " + article.getReference() + " existe déjà");
            return;
        }
        // AMELIORATION 2 : ajout du champ nom
        String sql = "INSERT INTO article (reference, nom, type, date_peremption, stock_minimal, critique) VALUES (?, ?, ?::type_article, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, article.getReference());
            stmt.setString(2, article.getNom());
            stmt.setString(3, article.getType().name().toLowerCase());
            stmt.setDate(4, java.sql.Date.valueOf(article.getDatePeremption()));
            stmt.setInt(5, article.getStockMinimal());
            stmt.setBoolean(6, article.isCritique());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) article.setId(rs.getInt(1));
            }
            System.out.println("OK Article ajouté ref=" + article.getReference());
        } catch (SQLException e) {
            System.err.println("Erreur ajout article: " + e.getMessage());
        }
    }

    public void modifier(Article article) {
        if (connection == null) return;
        String sql = "UPDATE article SET reference = ?, nom = ?, type = ?::type_article, date_peremption = ?, stock_minimal = ?, critique = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, article.getReference());
            stmt.setString(2, article.getNom());
            stmt.setString(3, article.getType().name().toLowerCase());
            stmt.setDate(4, java.sql.Date.valueOf(article.getDatePeremption()));
            stmt.setInt(5, article.getStockMinimal());
            stmt.setBoolean(6, article.isCritique());
            stmt.setInt(7, article.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur modifier article: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) return;
        String sql = "DELETE FROM article WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur supprimer article: " + e.getMessage());
        }
    }

    public List<Article> getAll() {
        List<Article> articles = new ArrayList<>();
        if (connection == null) return articles;
        String sql = "SELECT id, reference, nom, type, date_peremption, stock_minimal, critique FROM article ORDER BY reference";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                articles.add(new Article(
                        rs.getInt("id"),
                        rs.getInt("reference"),
                        rs.getString("nom"),
                        TypeArticle.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDate("date_peremption").toLocalDate(),
                        rs.getInt("stock_minimal"),
                        rs.getBoolean("critique")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAll articles: " + e.getMessage());
        }
        return articles;
    }

    // AMELIORATION : recherche par référence OU par nom
    public List<Article> rechercher(String critere) {
        List<Article> articles = new ArrayList<>();
        if (connection == null) return articles;
        String sql = "SELECT id, reference, nom, type, date_peremption, stock_minimal, critique FROM article WHERE CAST(reference AS TEXT) LIKE ? OR nom ILIKE ? ORDER BY reference";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere + "%");
            stmt.setString(2, "%" + critere + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("id"),
                            rs.getInt("reference"),
                            rs.getString("nom"),
                            TypeArticle.valueOf(rs.getString("type").toUpperCase()),
                            rs.getDate("date_peremption").toLocalDate(),
                            rs.getInt("stock_minimal"),
                            rs.getBoolean("critique")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur rechercher articles: " + e.getMessage());
        }
        return articles;
    }

    public Article getById(int id) {
        if (connection == null) return null;
        String sql = "SELECT id, reference, nom, type, date_peremption, stock_minimal, critique FROM article WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Article(
                            rs.getInt("id"),
                            rs.getInt("reference"),
                            rs.getString("nom"),
                            TypeArticle.valueOf(rs.getString("type").toUpperCase()),
                            rs.getDate("date_peremption").toLocalDate(),
                            rs.getInt("stock_minimal"),
                            rs.getBoolean("critique")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getById article: " + e.getMessage());
        }
        return null;
    }
}
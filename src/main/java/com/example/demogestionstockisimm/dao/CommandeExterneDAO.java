package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeExterneDAO {
    private Connection connection;
    private FournisseurDAO fournisseurDAO;
    private ArticleDAO articleDAO;
    private LocalDAO localDAO;

    public CommandeExterneDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
        this.fournisseurDAO = new FournisseurDAO();
        this.articleDAO = new ArticleDAO();
        this.localDAO = new LocalDAO();
    }

    public void ajouter(CommandeExterne commande) {
        if (connection == null) return;
        String sql = "INSERT INTO commande_externe (date_commande, statut, fournisseur_id) VALUES (?, ?::statut_commande, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, java.sql.Date.valueOf(commande.getDateCommande()));
            stmt.setString(2, "en_attente");
            stmt.setInt(3, commande.getFournisseur().getId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) commande.setId(rs.getInt(1));
            }
            System.out.println("OK Commande externe creee id=" + commande.getId());
        } catch (SQLException e) {
            System.err.println("Erreur ajouter commande externe: " + e.getMessage());
        }
    }

    public void ajouterLigneCommandePublic(int commandeId, LigneCommande ligne) {
        if (connection == null) return;
        String sql = "INSERT INTO ligne_commande (commande_externe_id, article_id, quantite, local_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, ligne.getArticle().getId());
            stmt.setInt(3, ligne.getQuantite());
            stmt.setInt(4, ligne.getLocal().getId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) ligne.setId(rs.getInt(1));
            }
            System.out.println("OK Ligne ajoutee commande=" + commandeId
                    + " article=" + ligne.getArticle().getId()
                    + " qte=" + ligne.getQuantite()
                    + " local=" + ligne.getLocal().getId());
        } catch (SQLException e) {
            System.err.println("Erreur ajouterLigneCommande: " + e.getMessage());
        }
    }

    public List<CommandeExterne> getAll() {
        List<CommandeExterne> commandes = new ArrayList<>();
        if (connection == null) return commandes;
        String sql = "SELECT id, date_commande, statut, fournisseur_id FROM commande_externe ORDER BY date_commande DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CommandeExterne commande = new CommandeExterne(
                        rs.getInt("id"),
                        rs.getDate("date_commande").toLocalDate(),
                        rs.getString("statut").toUpperCase(),
                        fournisseurDAO.getById(rs.getInt("fournisseur_id"))
                );
                commande.setLignes(getLignesCommande(commande.getId()));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAll commandes externes: " + e.getMessage());
        }
        return commandes;
    }

    private List<LigneCommande> getLignesCommande(int commandeId) {
        List<LigneCommande> lignes = new ArrayList<>();
        if (connection == null) return lignes;
        String sql = "SELECT id, article_id, quantite, local_id FROM ligne_commande WHERE commande_externe_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = articleDAO.getById(rs.getInt("article_id"));
                    Local local = localDAO.getById(rs.getInt("local_id"));
                    if (article != null && local != null) {
                        // CORRECTION : Constructeur avec 4 paramètres (id, quantite, article, local)
                        lignes.add(new LigneCommande(
                                rs.getInt("id"),
                                rs.getInt("quantite"),
                                article,
                                local
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getLignesCommande: " + e.getMessage());
        }
        return lignes;
    }

    public List<CommandeExterne> rechercher(String critere) {
        List<CommandeExterne> commandes = new ArrayList<>();
        if (connection == null) return commandes;
        String sql = "SELECT id, date_commande, statut, fournisseur_id FROM commande_externe WHERE fournisseur_id IN " +
                "(SELECT id FROM fournisseur WHERE nom ILIKE ?) ORDER BY date_commande DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CommandeExterne commande = new CommandeExterne(
                            rs.getInt("id"),
                            rs.getDate("date_commande").toLocalDate(),
                            rs.getString("statut").toUpperCase(),
                            fournisseurDAO.getById(rs.getInt("fournisseur_id"))
                    );
                    commande.setLignes(getLignesCommande(commande.getId()));
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur rechercher commandes externes: " + e.getMessage());
        }
        return commandes;
    }

    public void valider(int id) {
        if (connection == null) return;

        List<LigneCommande> lignes = getLignesCommande(id);
        System.out.println("valider commande #" + id + " -> " + lignes.size() + " lignes trouvees");

        if (lignes.isEmpty()) {
            System.err.println("ERREUR: Commande #" + id + " sans lignes en BD");
            return;
        }

        String sqlUpdate = "UPDATE commande_externe SET statut = 'validee'::statut_commande WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("OK Statut commande #" + id + " -> validee");
        } catch (SQLException e) {
            System.err.println("Erreur changement statut: " + e.getMessage());
            return;
        }

        StockDAO stockDAO = new StockDAO();
        for (LigneCommande ligne : lignes) {
            int articleId = ligne.getArticle().getId();
            int localId = ligne.getLocal().getId();
            int quantite = ligne.getQuantite();

            int stockExistant = stockDAO.getQuantiteByArticleAndLocal(articleId, localId);
            System.out.println("-> Article #" + articleId + " qte=" + quantite
                    + " stockExistant=" + stockExistant + " localId=" + localId);

            if (stockExistant >= 0) {
                stockDAO.incrementerQuantite(articleId, localId, quantite);
            } else {
                Stock nouveauStock = new Stock(ligne.getArticle(), ligne.getLocal(), quantite);
                stockDAO.ajouter(nouveauStock);
            }
        }
        System.out.println("OK Commande #" + id + " validee !");
    }

    public void annuler(int id) {
        if (connection == null) return;
        String sql = "UPDATE commande_externe SET statut = 'annulee'::statut_commande WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("OK Commande externe #" + id + " annulee");
        } catch (SQLException e) {
            System.err.println("Erreur annuler commande externe: " + e.getMessage());
        }
    }

    public CommandeExterne getCommandeById(int id) {
        if (connection == null) return null;
        String sql = "SELECT id, date_commande, statut, fournisseur_id FROM commande_externe WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CommandeExterne commande = new CommandeExterne(
                            rs.getInt("id"),
                            rs.getDate("date_commande").toLocalDate(),
                            rs.getString("statut").toUpperCase(),
                            fournisseurDAO.getById(rs.getInt("fournisseur_id"))
                    );
                    commande.setLignes(getLignesCommande(commande.getId()));
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getCommandeById: " + e.getMessage());
        }
        return null;
    }
}
package com.example.demogestionstockisimm.dao;

import com.example.demogestionstockisimm.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeInterneDAO {
    private Connection connection;
    private ServiceDAO serviceDAO;
    private ArticleDAO articleDAO;
    private LocalDAO localDAO;

    public CommandeInterneDAO() {
        this.connection = ConnexionDB.getInstance().getConnection();
        this.serviceDAO = new ServiceDAO();
        this.articleDAO = new ArticleDAO();
        this.localDAO = new LocalDAO();
    }

    public void ajouter(CommandeInterne commande) {
        if (connection == null) return;
        String sql = "INSERT INTO commande_interne (date_commande, statut, service_id) VALUES (?, ?::statut_commande, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, java.sql.Date.valueOf(commande.getDateCommande()));
            stmt.setString(2, "en_attente");
            stmt.setInt(3, commande.getService().getId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) commande.setId(rs.getInt(1));
            }
            System.out.println("OK Commande interne creee id=" + commande.getId());
        } catch (SQLException e) {
            System.err.println("Erreur ajouter commande interne: " + e.getMessage());
        }
    }

    public void ajouterLigneCommandePublic(int commandeId, LigneCommande ligne) {
        ajouterLigneCommande(commandeId, ligne);
    }

    // FIX : stocker local_id dans la ligne
    private void ajouterLigneCommande(int commandeId, LigneCommande ligne) {
        if (connection == null) return;
        String sql = "INSERT INTO ligne_commande (commande_interne_id, article_id, quantite, local_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, commandeId);
            stmt.setInt(2, ligne.getArticle().getId());
            stmt.setInt(3, ligne.getQuantite());
            if (ligne.getLocal() != null) {
                stmt.setInt(4, ligne.getLocal().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) ligne.setId(rs.getInt(1));
            }
            System.out.println("OK Ligne interne ajoutee commande=" + commandeId
                    + " article=" + ligne.getArticle().getId()
                    + " local=" + (ligne.getLocal() != null ? ligne.getLocal().getId() : "null"));
        } catch (SQLException e) {
            System.err.println("Erreur ajouterLigneCommande interne: " + e.getMessage());
        }
    }

    public void supprimerLignesCommande(int commandeId) {
        if (connection == null) return;
        String sql = "DELETE FROM ligne_commande WHERE commande_interne_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur supprimerLignes: " + e.getMessage());
        }
    }

    public void modifier(CommandeInterne commande) {
        if (connection == null) return;
        String sql = "UPDATE commande_interne SET date_commande = ?, service_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(commande.getDateCommande()));
            stmt.setInt(2, commande.getService().getId());
            stmt.setInt(3, commande.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur modifier commande interne: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        if (connection == null) return;
        supprimerLignesCommande(id);
        String sql = "DELETE FROM commande_interne WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur supprimer commande interne: " + e.getMessage());
        }
    }

    public List<CommandeInterne> getAll() {
        List<CommandeInterne> commandes = new ArrayList<>();
        if (connection == null) return commandes;
        String sql = "SELECT id, date_commande, statut, service_id FROM commande_interne ORDER BY date_commande DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CommandeInterne commande = new CommandeInterne(
                        rs.getInt("id"),
                        rs.getDate("date_commande").toLocalDate(),
                        rs.getString("statut").toUpperCase(),
                        serviceDAO.getById(rs.getInt("service_id"))
                );
                commande.setLignes(getLignesCommande(commande.getId()));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAll commandes internes: " + e.getMessage());
        }
        return commandes;
    }

    // FIX : charger aussi le local_id de chaque ligne
    private List<LigneCommande> getLignesCommande(int commandeId) {
        List<LigneCommande> lignes = new ArrayList<>();
        if (connection == null) return lignes;
        String sql = "SELECT id, article_id, quantite, local_id FROM ligne_commande WHERE commande_interne_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = articleDAO.getById(rs.getInt("article_id"));
                    if (article != null) {
                        LigneCommande ligne = new LigneCommande(
                                rs.getInt("id"),
                                rs.getInt("quantite"),
                                article
                        );
                        // FIX : charger le local propre à cette ligne
                        int localId = rs.getInt("local_id");
                        if (!rs.wasNull()) {
                            ligne.setLocal(localDAO.getById(localId));
                        }
                        lignes.add(ligne);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getLignesCommande interne: " + e.getMessage());
        }
        return lignes;
    }

    public List<CommandeInterne> rechercher(String critere) {
        List<CommandeInterne> commandes = new ArrayList<>();
        if (connection == null) return commandes;
        String sql = "SELECT id, date_commande, statut, service_id FROM commande_interne WHERE service_id IN " +
                "(SELECT id FROM service WHERE nom::text ILIKE ?) ORDER BY date_commande DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + critere + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CommandeInterne commande = new CommandeInterne(
                            rs.getInt("id"),
                            rs.getDate("date_commande").toLocalDate(),
                            rs.getString("statut").toUpperCase(),
                            serviceDAO.getById(rs.getInt("service_id"))
                    );
                    commande.setLignes(getLignesCommande(commande.getId()));
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur rechercher commandes internes: " + e.getMessage());
        }
        return commandes;
    }

    // FIX : chaque ligne utilise son propre local stocké en BD
    public boolean valider(int id) {
        if (connection == null) return false;

        List<LigneCommande> lignes = getLignesCommande(id);
        System.out.println("valider commande interne #" + id + " -> " + lignes.size() + " lignes");

        if (lignes.isEmpty()) {
            System.err.println("Commande interne #" + id + " sans lignes");
            return false;
        }

        // Vérifier que chaque ligne a bien un local
        for (LigneCommande ligne : lignes) {
            if (ligne.getLocal() == null) {
                System.err.println("Ligne sans local pour article #" + ligne.getArticle().getId());
                return false;
            }
        }

        StockDAO stockDAO = new StockDAO();

        // Vérifier stock pour chaque ligne dans SON local
        for (LigneCommande ligne : lignes) {
            int dispo = stockDAO.getQuantiteByArticleAndLocal(
                    ligne.getArticle().getId(), ligne.getLocal().getId()
            );
            System.out.println("-> Stock article #" + ligne.getArticle().getId()
                    + " dans local #" + ligne.getLocal().getId()
                    + " = " + dispo + " / demande=" + ligne.getQuantite());

            if (dispo < ligne.getQuantite()) {
                System.err.println("ERREUR: Stock insuffisant article #"
                        + ligne.getArticle().getId()
                        + " disponible=" + dispo
                        + " demande=" + ligne.getQuantite());
                return false;
            }
        }

        // Changer statut
        String sqlUpdate = "UPDATE commande_interne SET statut = 'validee'::statut_commande WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUpdate)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur changement statut: " + e.getMessage());
            return false;
        }

        // Décrémenter chaque ligne dans SON local
        for (LigneCommande ligne : lignes) {
            stockDAO.decrementerQuantite(
                    ligne.getArticle().getId(),
                    ligne.getLocal().getId(),
                    ligne.getQuantite()
            );
            System.out.println("OK Decremente article #" + ligne.getArticle().getId()
                    + " local #" + ligne.getLocal().getId()
                    + " -" + ligne.getQuantite());
        }
        System.out.println("OK Commande interne #" + id + " validee");
        return true;
    }

    // compatibilité ancienne signature
    public boolean valider(int id, Local local) {
        return valider(id);
    }

    public void annuler(int id) {
        if (connection == null) return;
        String sql = "UPDATE commande_interne SET statut = 'annulee'::statut_commande WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur annuler commande interne: " + e.getMessage());
        }
    }

    public CommandeInterne getCommandeById(int id) {
        if (connection == null) return null;
        String sql = "SELECT id, date_commande, statut, service_id FROM commande_interne WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CommandeInterne commande = new CommandeInterne(
                            rs.getInt("id"),
                            rs.getDate("date_commande").toLocalDate(),
                            rs.getString("statut").toUpperCase(),
                            serviceDAO.getById(rs.getInt("service_id"))
                    );
                    commande.setLignes(getLignesCommande(commande.getId()));
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getCommandeById interne: " + e.getMessage());
        }
        return null;
    }
}
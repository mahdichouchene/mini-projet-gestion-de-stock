package com.example.demogestionstockisimm.datastore;

import com.example.demogestionstockisimm.dao.*;
import com.example.demogestionstockisimm.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Consumer;

public class DataStore {

    private static DataStore instance;

    private ArticleDAO articleDAO;
    private FournisseurDAO fournisseurDAO;
    private ServiceDAO serviceDAO;
    private LocalDAO localDAO;
    private StockDAO stockDAO;
    private CommandeExterneDAO commandeExterneDAO;
    private CommandeInterneDAO commandeInterneDAO;

    private DataStore() {
        articleDAO = new ArticleDAO();
        fournisseurDAO = new FournisseurDAO();
        serviceDAO = new ServiceDAO();
        localDAO = new LocalDAO();
        stockDAO = new StockDAO();
        commandeExterneDAO = new CommandeExterneDAO();
        commandeInterneDAO = new CommandeInterneDAO();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ===== METHODES GENERIQUES =====

    public <T> List<T> filtrer(List<T> liste, Predicate<T> predicate) {
        List<T> resultat = new ArrayList<>();
        for (T element : liste) {
            if (predicate.test(element)) resultat.add(element);
        }
        return resultat;
    }

    public <T> void supprimer(List<T> liste, Predicate<T> predicate) {
        liste.removeIf(predicate);
    }

    public <T> void modifier(List<T> liste, Predicate<T> predicate, Consumer<T> action) {
        for (T element : liste) {
            if (predicate.test(element)) { action.accept(element); return; }
        }
    }

    // ==================== ARTICLES ====================

    public void ajouterArticle(Article a) { articleDAO.ajouter(a); }
    public void modifierArticle(Article a) { articleDAO.modifier(a); }
    public void supprimerArticle(int id) { articleDAO.supprimer(id); }
    public List<Article> getArticles() { return articleDAO.getAll(); }
    public List<Article> rechercherArticles(String critere) { return articleDAO.rechercher(critere); }

    public List<Article> getArticlesEnAlerte() {
        List<Article> alertes = new ArrayList<>();
        for (Stock s : stockDAO.getAll()) {
            if (s.getQuantite() < s.getArticle().getStockMinimal())
                alertes.add(s.getArticle());
        }
        return alertes;
    }

    public List<Article> getArticlesProchesPeremption() {
        LocalDate seuil = LocalDate.now().plusDays(30);
        List<Article> proches = new ArrayList<>();
        for (Stock s : stockDAO.getAll()) {
            Article a = s.getArticle();
            if (a.getDatePeremption() != null &&
                    a.getDatePeremption().isAfter(LocalDate.now()) &&
                    a.getDatePeremption().isBefore(seuil))
                proches.add(a);
        }
        return proches;
    }

    public List<Article> getArticlesPerimees() {
        List<Article> perimees = new ArrayList<>();
        for (Stock s : stockDAO.getAll()) {
            Article a = s.getArticle();
            if (a.getDatePeremption() != null && a.getDatePeremption().isBefore(LocalDate.now()))
                perimees.add(a);
        }
        return perimees;
    }

    public List<Article> getArticlesCritiques() {
        return filtrer(articleDAO.getAll(), a -> a.isCritique());
    }

    // ==================== FOURNISSEURS ====================

    public void ajouterFournisseur(Fournisseur f) { fournisseurDAO.ajouter(f); }
    public void modifierFournisseur(Fournisseur f) { fournisseurDAO.modifier(f); }
    public void supprimerFournisseur(int id) { fournisseurDAO.supprimer(id); }
    public List<Fournisseur> getFournisseurs() { return fournisseurDAO.getAll(); }
    public List<Fournisseur> rechercherFournisseurs(String critere) { return fournisseurDAO.rechercher(critere); }

    // ==================== SERVICES ====================

    public void ajouterService(Service s) { serviceDAO.ajouter(s); }
    public void modifierService(Service s) { serviceDAO.modifier(s); }
    public void supprimerService(int id) { serviceDAO.supprimer(id); }
    public List<Service> getServices() { return serviceDAO.getAll(); }
    public List<Service> rechercherServices(String critere) { return serviceDAO.rechercher(critere); }

    // ==================== LOCAUX ====================

    public void ajouterLocal(Local l) { localDAO.ajouter(l); }
    public void modifierLocal(Local l) { localDAO.modifier(l); }
    public void supprimerLocal(int id) { localDAO.supprimer(id); }
    public List<Local> getLocaux() { return localDAO.getAll(); }
    public List<Local> rechercherLocaux(String critere) { return localDAO.rechercher(critere); }

    // ==================== STOCKS ====================

    public void ajouterStock(Stock s) { stockDAO.ajouter(s); }
    public void supprimerStock(int id) { stockDAO.supprimer(id); }
    public List<Stock> getStocks() { return stockDAO.getAll(); }

    public Stock getStockParArticle(int articleId) {
        List<Stock> r = filtrer(stockDAO.getAll(), s -> s.getArticle().getId() == articleId);
        return r.isEmpty() ? null : r.get(0);
    }

    public Stock getStockParArticleEtLocal(int articleId, int localId) {
        List<Stock> r = filtrer(stockDAO.getAll(),
                s -> s.getArticle().getId() == articleId && s.getLocal().getId() == localId);
        return r.isEmpty() ? null : r.get(0);
    }

    // ==================== COMMANDES EXTERNES ====================

    public void ajouterCommandeExterne(CommandeExterne c) { commandeExterneDAO.ajouter(c); }
    //public void supprimerCommandeExterne(int id) { commandeExterneDAO.supprimer(id); }
    public List<CommandeExterne> getCommandesExternes() { return commandeExterneDAO.getAll(); }
    public List<CommandeExterne> rechercherCommandesExternes(String c) { return commandeExterneDAO.rechercher(c); }

    // local par ligne — pas de local global
    public void validerCommandeExterne(int id) { commandeExterneDAO.valider(id); }
    // compatibilité ancienne signature
    public void validerCommandeExterne(int id, Local local) { commandeExterneDAO.valider(id); }

    public void annulerCommandeExterne(int id) { commandeExterneDAO.annuler(id); }

    public void ajouterLigneCommandeExterne(int commandeId, LigneCommande ligne) {
        commandeExterneDAO.ajouterLigneCommandePublic(commandeId, ligne);
    }

    // ==================== COMMANDES INTERNES ====================

    public void ajouterCommandeInterne(CommandeInterne c) { commandeInterneDAO.ajouter(c); }
    public void supprimerCommandeInterne(int id) { commandeInterneDAO.supprimer(id); }
    public List<CommandeInterne> getCommandesInternes() { return commandeInterneDAO.getAll(); }
    public List<CommandeInterne> rechercherCommandesInternes(String c) { return commandeInterneDAO.rechercher(c); }

    // FIX : local par ligne — valider() sans local global, le DAO gère le local de chaque ligne
    public boolean validerCommandeInterne(int id) {
        return commandeInterneDAO.valider(id);
    }

    // compatibilité ancienne signature — ignorée, chaque ligne a son local
    public boolean validerCommandeInterne(int id, Local local) {
        return commandeInterneDAO.valider(id);
    }

    public void annulerCommandeInterne(int id) { commandeInterneDAO.annuler(id); }

    public void ajouterLigneCommandeInterne(int commandeId, LigneCommande ligne) {
        commandeInterneDAO.ajouterLigneCommandePublic(commandeId, ligne);
    }

    // ==================== STATISTIQUES ====================

    public Article getProduitPlusConsomme() {
        List<CommandeInterne> commandes = filtrer(
                commandeInterneDAO.getAll(), c -> c.getStatut().equals("VALIDEE"));
        Article plusConsomme = null;
        int maxQuantite = 0;
        for (CommandeInterne c : commandes) {
            for (LigneCommande ligne : c.getLignes()) {
                int total = getTotalConsomme(ligne.getArticle());
                if (total > maxQuantite) { maxQuantite = total; plusConsomme = ligne.getArticle(); }
            }
        }
        return plusConsomme;
    }

    public Article getProduitPlusConsommeParService(Service service) {
        List<CommandeInterne> commandesService = filtrer(
                commandeInterneDAO.getAll(),
                c -> c.getStatut().equals("VALIDEE") &&
                        c.getService() != null &&
                        c.getService().getId() == service.getId());
        Article plusConsomme = null;
        int maxQuantite = 0;
        for (CommandeInterne c : commandesService) {
            for (LigneCommande ligne : c.getLignes()) {
                int total = 0;
                for (CommandeInterne ci : commandesService)
                    for (LigneCommande l : ci.getLignes())
                        if (l.getArticle().getId() == ligne.getArticle().getId())
                            total += l.getQuantite();
                if (total > maxQuantite) { maxQuantite = total; plusConsomme = ligne.getArticle(); }
            }
        }
        return plusConsomme;
    }

    public int getTotalConsomme(Article article) {
        int total = 0;
        List<CommandeInterne> commandes = filtrer(
                commandeInterneDAO.getAll(), c -> c.getStatut().equals("VALIDEE"));
        for (CommandeInterne c : commandes)
            for (LigneCommande l : c.getLignes())
                if (l.getArticle().getId() == article.getId())
                    total += l.getQuantite();
        return total;
    }
}
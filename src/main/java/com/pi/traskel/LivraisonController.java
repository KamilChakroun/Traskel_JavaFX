package com.pi.traskel;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LivraisonController implements Initializable {

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private TableColumn<Livraison, Integer> colID;

    @FXML
    private TableColumn<Livraison, User> colLIV;

    @FXML
    private TableColumn<Livraison, Commande> colCMD;

    @FXML
    private TableColumn<Livraison, String> colADR;

    @FXML
    private TableColumn<Livraison, String> colDELAI;

    @FXML
    private TableColumn<Livraison, String> colSTATUT;

    @FXML
    private ComboBox<Commande> commandeCB;

    @FXML
    private ComboBox<User> livreurCB;

    @FXML
    private Label mainLabel;

    @FXML
    private TableView<Livraison> table;

    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherLivraisons();
        commandeCB.getItems().addAll(getAllCommandes());
        livreurCB.getItems().addAll(getAllLivreurUsers());
    }

    private List<Commande> getAllCommandes() {
        List<Commande> allCommandes = new ArrayList<>();

        try (Connection con = DBConnection.getCon();
             PreparedStatement st = con.prepareStatement("SELECT * FROM commande");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setAdresse(rs.getString("adresse"));
                commande.setStatut(rs.getString("statut"));
                commande.setDelai(rs.getString("delai"));

                Panier panier = getPanierByCommandeId(commande.getId());
                commande.setPanier(panier);

                allCommandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allCommandes;
    }

    private List<User> getAllLivreurUsers() {
        List<User> allLivreurUsers = new ArrayList<>();

        try (Connection con = DBConnection.getCon();
             PreparedStatement st = con.prepareStatement("SELECT * FROM user WHERE role = ?");
        ) {
            st.setString(1, "livreur");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    User livreur = new User();
                    livreur.setId(rs.getInt("id"));
                    livreur.setPrenom(rs.getString("prenom"));
                    livreur.setNom(rs.getString("nom"));
                    livreur.setEmail(rs.getString("email"));
                    livreur.setPassword(rs.getString("password"));
                    livreur.setAdresse(rs.getString("adresse"));
                    livreur.setRole(rs.getString("role"));
                    allLivreurUsers.add(livreur);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allLivreurUsers;
    }

    private Panier getPanierByCommandeId(int commandeId) {
        try (Connection con = DBConnection.getCon();
             PreparedStatement st = con.prepareStatement("SELECT * FROM panier WHERE commande_id = ?");
        ) {
            st.setInt(1, commandeId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Panier panier = new Panier();
                    panier.setId(rs.getInt("id"));
                    return panier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User fetchLivreurById(int livreurId) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, livreurId);
        ResultSet rs = st.executeQuery();

        User livreur = null;
        if (rs.next()) {
            livreur = new User();
            livreur.setId(rs.getInt("id"));
            livreur.setPrenom(rs.getString("prenom"));
            livreur.setNom(rs.getString("nom"));
            livreur.setEmail(rs.getString("email"));
            livreur.setPassword(rs.getString("password"));
            livreur.setAdresse(rs.getString("adresse"));
            livreur.setRole(rs.getString("role"));
        }

        rs.close();
        st.close();

        return livreur;
    }

    public Commande fetchCommandeById(int commandeId) throws SQLException {
        String query = "SELECT * FROM commande WHERE id = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, commandeId);
        ResultSet rs = st.executeQuery();

        Commande commande = null;
        if (rs.next()) {
            commande = new Commande();
            commande.setId(rs.getInt("id"));
            commande.setAdresse(rs.getString("adresse"));
            commande.setStatut(rs.getString("statut"));
            commande.setDelai(rs.getString("delai"));

            Panier panier = getPanierByCommandeId(commande.getId());
            commande.setPanier(panier);
        }

        rs.close();
        st.close();

        return commande;
    }

    public ObservableList<Livraison> getLivraisons(){
        ObservableList<Livraison> livraisons = FXCollections.observableArrayList();

        String query = "select * from livraisons";
        con = DBConnection.getCon();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while(rs.next()){
                Livraison liv = new Livraison();

                liv.setId(rs.getInt("id"));

                int livreurId = rs.getInt("livreur_id");
                User livreur = fetchLivreurById(livreurId);
                liv.setLivreur(livreur);

                int commandeId = rs.getInt("commande_id");
                Commande commande = fetchCommandeById(commandeId);
                liv.setCommande(commande);

                livraisons.add(liv);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return livraisons;
    }

    public void afficherLivraisons(){
        ObservableList<Livraison> list = getLivraisons();
        table.setItems(list);
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLIV.setCellValueFactory(new PropertyValueFactory<>("livreur"));

        colADR.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCommande() != null) {
                return new SimpleStringProperty(cellData.getValue().getCommande().getAdresse());
            } else {
                return new SimpleStringProperty("");
            }
        });

        colSTATUT.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCommande() != null) {
                return new SimpleStringProperty(cellData.getValue().getCommande().getStatut());
            } else {
                return new SimpleStringProperty("");
            }
        });

        colDELAI.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCommande() != null) {
                return new SimpleStringProperty(cellData.getValue().getCommande().getDelai());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    @FXML
    void ajouterLivraison(ActionEvent event) {
        Commande selectedCommande = commandeCB.getSelectionModel().getSelectedItem();
        User selectedLivreur = livreurCB.getSelectionModel().getSelectedItem();

        if (selectedCommande != null && selectedLivreur != null) {
            String query = "INSERT INTO livraisons (livreur_id, commande_id) VALUES (?, ?)";
            try {
                con = DBConnection.getCon();
                st = con.prepareStatement(query);

                st.setInt(1, selectedLivreur.getId());
                st.setInt(2, selectedCommande.getId());

                st.executeUpdate();

                afficherLivraisons();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Please select both a Commande and a Livreur.");
        }
    }

    @FXML
    void updateLivraison(ActionEvent event) {
        Livraison selectedLivraison = table.getSelectionModel().getSelectedItem();

        if (selectedLivraison != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/UpdateLivraison.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                UpdateLivController updateLivController = fxmlLoader.getController();
                updateLivController.setLivraison(selectedLivraison);
                Stage stage = new Stage();
                stage.setTitle("Modifier Livraison");
                stage.setScene(new Scene(root1));

                stage.setOnHidden(e -> {
                    // Update the table view after the window is closed
                    afficherLivraisons();
                });

                stage.showAndWait();
            } catch (Exception e) {
                System.out.println("Can't load new window");
            }
        } else {
            System.out.println("Please select a Livraison to update.");
        }
    }



    @FXML
    void supprimerLivraison(ActionEvent event) {
        Livraison selectedLivraison = table.getSelectionModel().getSelectedItem();

        if (selectedLivraison != null) {
            String query = "DELETE FROM livraisons WHERE id = ?";
            try {
                con = DBConnection.getCon();
                st = con.prepareStatement(query);

                st.setInt(1, selectedLivraison.getId());

                st.executeUpdate();

                afficherLivraisons();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Please select a Livraison to delete.");
        }
    }

}
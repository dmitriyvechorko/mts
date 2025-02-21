package mts.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import mts.dao.ClientDAO;
import mts.models.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

@Setter
public class ClientController {
    private Stage primaryStage;

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, Integer> idColumn;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> addressColumn;
    @FXML
    private TableColumn<Client, LocalDate> dateColumn;

    @FXML
    private VBox addClientForm;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField addressField;

    private final ClientDAO clientDAO = new ClientDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        loadClients();
    }

    private void loadClients() {
        ObservableList<Client> clients = FXCollections.observableArrayList(clientDAO.getAllClients());
        clientTable.setItems(clients);
    }

    @FXML
    private void addClient() {
        addClientForm.setVisible(true);

        fullNameField.clear();
        addressField.clear();

        addClientForm.setUserData(null);
    }



    @FXML
    private void saveClient() {
        String fullName = fullNameField.getText();
        String address = addressField.getText();

        if (fullName.isEmpty() || address.isEmpty()) {
            showAlert("Ошибка", "Все поля должны быть заполнены.");
            return;
        }

        Client clientToSave;
        if (addClientForm.getUserData() == null) {

            clientToSave = new Client(0, fullName, address, null); // Дата не передается
            clientDAO.createClient(clientToSave);
        } else {

            clientToSave = (Client) addClientForm.getUserData();
            clientToSave.setFullName(fullName);
            clientToSave.setAddress(address);
            clientDAO.updateClient(clientToSave);
        }

        addClientForm.setVisible(false);

        loadClients();

        showAlert("Успех", "Данные клиента сохранены.");
    }

    @FXML
    private void cancel() {
        addClientForm.setVisible(false);
    }

    @FXML
    private void editClient() {

        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {

            addClientForm.setVisible(true);


            fullNameField.setText(selectedClient.getFullName());
            addressField.setText(selectedClient.getAddress());

            addClientForm.setUserData(selectedClient);
        } else {
            showAlert("Ошибка", "Выберите клиента для редактирования.");
        }
    }

    @FXML
    private void deleteClient() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            clientDAO.deleteClient(selectedClient.getId());
            loadClients();
        } else {
            showAlert("Ошибка", "Выберите клиента для удаления.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void goBackToMainMenu() {
        switchToMainMenu();
    }

    private void switchToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setPrimaryStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Главное меню");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
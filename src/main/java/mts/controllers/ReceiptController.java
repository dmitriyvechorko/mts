package mts.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Setter;
import mts.dao.ReceiptDAO;
import mts.models.Receipt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
public class ReceiptController {

    private Stage primaryStage;

    @FXML
    private TableView<Receipt> receiptTable;
    @FXML
    private TableColumn<Receipt, Integer> idColumn;
    @FXML
    private TableColumn<Receipt, Integer> conversationIdColumn;
    @FXML
    private TableColumn<Receipt, LocalDate> receiptDateColumn;
    @FXML
    private TableColumn<Receipt, Integer> costIdColumn;
    @FXML
    private TableColumn<Receipt, BigDecimal> totalCostColumn;

    @FXML
    private TableColumn<Receipt, Boolean> paidColumn;

    private final ReceiptDAO receiptDAO = new ReceiptDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        conversationIdColumn.setCellValueFactory(new PropertyValueFactory<>("conversationId"));
        receiptDateColumn.setCellValueFactory(new PropertyValueFactory<>("receiptDate"));
        costIdColumn.setCellValueFactory(new PropertyValueFactory<>("costId"));
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        loadReceipts();
    }


    private void loadReceipts() {
        ObservableList<Receipt> receipts = FXCollections.observableArrayList(receiptDAO.getAllReceipts());
        receiptTable.setItems(receipts);
    }

    @FXML
    private void markAsPaid() {
        Receipt selectedReceipt = receiptTable.getSelectionModel().getSelectedItem();
        if (selectedReceipt != null) {
            selectedReceipt.setPaid(true); // Изменяем статус на "оплачено"
            receiptDAO.updateReceipt(selectedReceipt); // Обновляем квитанцию в базе данных
            loadReceipts(); // Перезагружаем список квитанций
            showAlert("Успех", "Квитанция помечена как оплаченная.");
        } else {
            showAlert("Ошибка", "Выберите квитанцию для пометки как оплаченная.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

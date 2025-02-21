package mts.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import mts.dao.CostDAO;
import mts.models.Cost;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
public class CostController {

    private Stage primaryStage;

    @FXML
    private HBox datePickerBox;
    @FXML
    private DatePicker dateField;

    @FXML
    private TableView<Cost> costTable;

    @FXML
    private TableColumn<Cost, Integer> idColumn;
    @FXML
    private TableColumn<Cost, LocalDate> dateColumn;
    @FXML
    private TableColumn<Cost, String> settlementNameColumn;
    @FXML
    private TableColumn<Cost, String> costPerMinColumn;
    @FXML
    private TableColumn<Cost, String> preferentialCostColumn;

    @FXML
    private TextField settlementNameField;
    @FXML
    private TextField costPerMinField;
    @FXML
    private TextField preferentialCostField;

    @FXML
    private VBox addCostForm;

    private final CostDAO costDAO = new CostDAO();

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        settlementNameColumn.setCellValueFactory(new PropertyValueFactory<>("settlementName"));
        costPerMinColumn.setCellValueFactory(new PropertyValueFactory<>("costPerMin"));
        preferentialCostColumn.setCellValueFactory(new PropertyValueFactory<>("preferentialCost"));

        loadCosts();
    }

    private void loadCosts() {
        List<Cost> costs = costDAO.getAllCosts();
        ObservableList<Cost> costList = FXCollections.observableArrayList(costs);
        costTable.setItems(costList);
    }

    @FXML
    private void addCost() {
        addCostForm.setVisible(true);

        settlementNameField.clear();
        dateField.setValue(null);
        costPerMinField.clear();
        preferentialCostField.clear();

        addCostForm.setUserData(null);
    }

    @FXML
    private void saveCost() {

        LocalDate date = dateField.getValue();
        String settlementName = settlementNameField.getText();
        String costPerMinString = costPerMinField.getText();
        String preferentialCostString = preferentialCostField.getText();


        if (costPerMinString.isEmpty() || preferentialCostString.isEmpty() || settlementName.isEmpty() || date == null) {
            showAlert("Ошибка", "Все поля должны быть заполнены и дата выбрана.");
            return;
        }


        BigDecimal costPerMin;
        BigDecimal preferentialCost;
        try {
            costPerMin = new BigDecimal(costPerMinString);
            preferentialCost = new BigDecimal(preferentialCostString);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректный формат стоимости.");
            return;
        }


        Cost costToSave;
        if (addCostForm.getUserData() == null) {

            costToSave = new Cost(0, date, settlementName, costPerMin, preferentialCost);
            costDAO.createCost(costToSave);
        } else {

            costToSave = (Cost) addCostForm.getUserData();
            costToSave.setDate(date);
            costToSave.setSettlementName(settlementName);
            costToSave.setCostPerMin(costPerMin);
            costToSave.setPreferentialCost(preferentialCost);
            costDAO.updateCost(costToSave);
        }

        addCostForm.setVisible(false);

        loadCosts();

        showAlert("Успех", "Данные стоимости сохранены.");
    }


    @FXML
    private void showDateField() {
        datePickerBox.setVisible(true);
    }

    @FXML
    private void showCostByDate() {

        if (dateField.getValue() == null) {
            showAlert("Ошибка", "Выберите дату.");
            return;
        }

        LocalDate selectedDate = dateField.getValue();

        List<Cost> costs = getCostByDate(selectedDate);

        if (costs.isEmpty()) {
            showAlert("Информация", "Нет данных о стоимости на выбранную дату.");
            return;
        }

        ObservableList<Cost> costData = FXCollections.observableArrayList(costs);
        costTable.setItems(costData);
    }

    private List<Cost> getCostByDate(LocalDate date) {
        return costDAO.getCostsByDate(date);
    }

    @FXML
    private void deleteCost() {

        Cost selectedCost = costTable.getSelectionModel().getSelectedItem();
        if (selectedCost != null) {
            costDAO.deleteCost(selectedCost.getId());
            loadCosts();
        } else {
            showAlert("Ошибка", "Выберите запись о стоимости для удаления.");
        }
    }

    @FXML
    private void editCost() {

        Cost selectedCost = costTable.getSelectionModel().getSelectedItem();
        if (selectedCost != null) {

            addCostForm.setVisible(true);


            settlementNameField.setText(selectedCost.getSettlementName());
            dateField.setValue(selectedCost.getDate());
            costPerMinField.setText(String.valueOf(selectedCost.getCostPerMin()));
            preferentialCostField.setText(String.valueOf(selectedCost.getPreferentialCost()));


            addCostForm.setUserData(selectedCost);
        } else {
            showAlert("Ошибка", "Выберите запись о стоимости для редактирования.");
        }
    }

    @FXML
    private void cancel() {
        addCostForm.setVisible(false);
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

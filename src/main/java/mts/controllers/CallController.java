package mts.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import mts.dao.ClientDAO;
import mts.dao.CallDAO;
import mts.models.Client;
import mts.models.Call;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;

@Setter
public class CallController {

    private Stage primaryStage;

    @FXML
    private TextField cityForCalculationField;
    @FXML
    private ComboBox<String> monthForCalculationComboBox;
    @FXML
    private Label subscriberCountLabel;
    @FXML
    private VBox subscriberCalculationForm;
    @FXML
    private TableView<Call> callTable;
    @FXML
    private TableColumn<Call, Integer> idColumn;
    @FXML
    private TableColumn<Call, String> cityCalledToColumn;
    @FXML
    private TableColumn<Call, String> phoneNumberColumn;
    @FXML
    private TableColumn<Call, LocalDateTime> dateOfCallColumn;
    @FXML
    private TableColumn<Call, String> callDurationColumn;
    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private TextField cityCalledToField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private DatePicker dateOfCallField;
    @FXML
    private TextField timeOfCallField;
    @FXML
    private TextField callDurationField;
    @FXML
    private VBox addCallForm;

    private final ClientDAO clientDAO = new ClientDAO();
    private final CallDAO callDAO = new CallDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cityCalledToColumn.setCellValueFactory(new PropertyValueFactory<>("cityCalledTo"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        dateOfCallColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfCall"));
        callDurationColumn.setCellValueFactory(new PropertyValueFactory<>("callDuration"));
        loadClientsForComboBox();
        loadCalls();
    }


    @FXML
    private void showSubscriberCalculationForm() {
        subscriberCalculationForm.setVisible(true);
    }

    @FXML
    private void calculateSubscriberCount() {
        String city = cityForCalculationField.getText();
        String monthString = monthForCalculationComboBox.getValue();

        if (city.isEmpty() || monthString == null) {
            showAlert("Ошибка", "Пожалуйста, укажите город и месяц.");
            return;
        }


        Month month = Month.valueOf(monthString.toUpperCase());


        int subscriberCount = callDAO.getSubscriberCountForCityAndMonth(city, month);


        subscriberCountLabel.setText("Количество абонентов: " + subscriberCount);
        subscriberCountLabel.setVisible(true);
    }


    private void loadClientsForComboBox() {
        ClientDAO clientDAO = new ClientDAO();
        List<Client> clients = clientDAO.getAllClients();
        ObservableList<Client> clientList = FXCollections.observableArrayList(clients);
        clientComboBox.setItems(clientList);


        clientComboBox.setCellFactory(param -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getFullName());
            }
        });
    }

    @FXML
    private void addCall() {

        addCallForm.setVisible(true);


        cityCalledToField.clear();
        phoneNumberField.clear();
        dateOfCallField.setValue(null);
        callDurationField.clear();


        clientComboBox.getSelectionModel().clearSelection();


        addCallForm.setUserData(null);
    }


    @FXML
    private void saveCall() {

        Client selectedClient = clientComboBox.getValue();
        String cityCalledTo = cityCalledToField.getText();
        String phoneNumber = phoneNumberField.getText();
        LocalDate date = dateOfCallField.getValue();
        String callDurationString = callDurationField.getText();


        if (selectedClient == null || cityCalledTo.isEmpty() || phoneNumber.isEmpty() || date == null || callDurationString.isEmpty()) {
            showAlert("Ошибка", "Все поля должны быть заполнены.");
            return;
        }


        LocalTime time;
        try {
            time = LocalTime.parse(timeOfCallField.getText());
        } catch (DateTimeParseException e) {
            showAlert("Ошибка", "Некорректный формат времени. Используйте формат ЧЧ:ММ.");
            return;
        }

        LocalDateTime dateTimeOfCall = LocalDateTime.of(date, time);

        LocalTime callDuration;
        try {
            callDuration = LocalTime.parse(callDurationString);
        } catch (DateTimeParseException e) {
            showAlert("Ошибка", "Некорректный формат времени вызова. Ожидаемый формат: HH:mm:ss.");
            return;
        }

        Call newCall = new Call(0, selectedClient.getId(), dateTimeOfCall, cityCalledTo, phoneNumber, callDuration);
        CallDAO callDAO = new CallDAO();
        callDAO.createCall(newCall);


        addCallForm.setVisible(false);


        loadCalls();


        showAlert("Успех", "Звонок успешно добавлен.");
    }


    @FXML
    private void deleteCall() {
        Call selectedCall = callTable.getSelectionModel().getSelectedItem();
        if (selectedCall != null) {
            callDAO.deleteCall(selectedCall.getId());
            loadCalls();
        } else {
            showAlert("Ошибка", "Выберите звонок для удаления.");
        }
    }

    private void showAlert(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadCalls() {
        CallDAO callDAO = new CallDAO();
        List<Call> calls = callDAO.getAllCalls();
        ObservableList<Call> callList = FXCollections.observableArrayList(calls);
        callTable.setItems(callList);
    }

    @FXML
    private void cancel() {
        addCallForm.setVisible(false);
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

    @FXML
    private void editCall() {

        Call selectedCall = callTable.getSelectionModel().getSelectedItem();

        if (selectedCall != null) {

            addCallForm.setVisible(true);


            cityCalledToField.setText(selectedCall.getCityCalledTo());
            phoneNumberField.setText(selectedCall.getPhoneNumber());
            dateOfCallField.setValue(selectedCall.getDateOfCall().toLocalDate());
            callDurationField.setText(selectedCall.getCallDuration().toString());


            addCallForm.setUserData(selectedCall);
        } else {
            showAlert("Ошибка", "Выберите звонок для редактирования.");
        }
    }
}

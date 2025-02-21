package mts.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Data;


@Data
public class MainController {

    private Stage primaryStage;

    public void openClientView() {
        switchScene("/fxml/ClientView.fxml", "Управление клиентами");
    }

    public void openCallView() {
        switchScene("/fxml/CallView.fxml", "Управление звонками");
    }

    public void openCostView() {
        switchScene("/fxml/CostView.fxml", "Управление стоимостью");
    }
    public void openReceiptView() {
        switchScene("/fxml/ReceiptView.fxml", "Управление квитанциями");
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ClientController) {
                ((ClientController) controller).setPrimaryStage(primaryStage);
            } else if (controller instanceof CallController) {
                ((CallController) controller).setPrimaryStage(primaryStage);
            } else if (controller instanceof CostController) {
                ((CostController) controller).setPrimaryStage(primaryStage);
            } else if (controller instanceof ReceiptController) {
                ((ReceiptController) controller).setPrimaryStage(primaryStage);
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
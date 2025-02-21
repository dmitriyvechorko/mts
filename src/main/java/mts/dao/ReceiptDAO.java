package mts.dao;

import mts.config.DatabaseConnection;
import mts.models.Receipt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDAO {


    public List<Receipt> getAllReceipts() {
        List<Receipt> receipts = new ArrayList<>();
        String query = "SELECT * FROM receipt";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Receipt receipt = new Receipt(
                        resultSet.getInt("id"),
                        resultSet.getInt("conversation_id"),
                        resultSet.getDate("receipt_date").toLocalDate(),
                        resultSet.getInt("cost_id"),
                        resultSet.getBigDecimal("total_cost"),
                        resultSet.getBoolean("paid")
                );
                receipts.add(receipt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receipts;
    }


    public Receipt getReceiptById(int receiptId) {
        String query = "SELECT * FROM receipt WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, receiptId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Receipt(
                        resultSet.getInt("id"),
                        resultSet.getInt("conversation_id"),
                        resultSet.getDate("receipt_date").toLocalDate(),
                        resultSet.getInt("cost_id"),
                        resultSet.getBigDecimal("total_cost"),
                        resultSet.getBoolean("paid")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateReceipt(Receipt receipt) {
        String query = "UPDATE receipt SET paid = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBoolean(1, receipt.isPaid());
            preparedStatement.setInt(2, receipt.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Receipt updated successfully!");
            } else {
                System.out.println("Receipt not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating receipt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

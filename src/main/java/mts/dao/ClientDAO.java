package mts.dao;

import mts.config.DatabaseConnection;
import mts.models.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Client client = new Client(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("address"),
                        resultSet.getDate("registration_date").toLocalDate() // Добавляем дату для отображения
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public Client getClientById(int clientId) {
        String query = "SELECT * FROM client WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Client(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("address"),
                        resultSet.getDate("registration_date").toLocalDate()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createClient(Client client) {
        String procedureCall = "CALL add_client(?, ?)";  // Правильный вызов процедуры в PostgreSQL

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {


            callableStatement.setString(1, client.getFullName());
            callableStatement.setString(2, client.getAddress());


            callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClient(Client client) {
        String query = "UPDATE client SET full_name = ?, address = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, client.getFullName());
            statement.setString(2, client.getAddress());
            statement.setInt(3, client.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Client updated successfully!");
            } else {
                System.out.println("Client not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteClient(int clientId) {
        String query = "DELETE FROM client WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, clientId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
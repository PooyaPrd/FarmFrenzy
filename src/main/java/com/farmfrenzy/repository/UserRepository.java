package com.farmfrenzy.repository;

import com.farmfrenzy.model.User;
import com.farmfrenzy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public boolean saveUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Could not save user: " + e.getMessage());
            return false;
        }
    }

    public User getUser(String username) {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Could not read user: " + e.getMessage());
            return null;
        }
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Could not validate user: " + e.getMessage());
            return false;
        }
    }
}

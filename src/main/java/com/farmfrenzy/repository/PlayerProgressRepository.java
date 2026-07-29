package com.farmfrenzy.repository;

import com.farmfrenzy.model.PlayerProgress;
import com.farmfrenzy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerProgressRepository {

    public boolean saveProgress(int userId, int level, int coins) {
        String sql = "INSERT INTO player_progress (user_id, current_level, coins) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE current_level = ?, coins = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, level);
            statement.setInt(3, coins);
            statement.setInt(4, level);
            statement.setInt(5, coins);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Could not save progress: " + e.getMessage());
            return false;
        }
    }

    public PlayerProgress getProgressByUserId(int userId) {
        String sql = "SELECT user_id, current_level, coins FROM player_progress WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new PlayerProgress(rs.getInt("user_id"), rs.getInt("current_level"), rs.getInt("coins"));
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

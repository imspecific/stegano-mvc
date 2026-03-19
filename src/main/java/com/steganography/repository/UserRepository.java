package com.steganography.repository;

import com.steganography.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User findByUsernameAndPassword(String username, String password) {
        String query = "SELECT Userid, F_name, L_name, Username " +
                       "FROM reg WHERE Username = ? AND Password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("Userid"),
                        rs.getString("F_name"),
                        rs.getString("L_name"),
                        rs.getString("Username")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Login query failed: " + e.getMessage());
        }

        return null; // credentials not found
    }

    public boolean registerUser(String firstName, String lastName,
                                 String username, String password) {
        String query = "INSERT INTO reg (F_name, L_name, Username, Password) " +
                       "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
}

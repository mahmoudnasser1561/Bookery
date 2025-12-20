package code.demo.dao;

import code.demo.core.DatabaseConnection;
import code.demo.model.Role;
import code.demo.model.User;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT id, username, password, role FROM users WHERE username=? AND password=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            Role.valueOf(rs.getString("role"))
                    );
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AUTH ERROR] " + e.getMessage());
        }
        return Optional.empty();
    }

    // Public registration: role must be USER by default
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, "USER");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("[REGISTER ERROR] " + e.getMessage());
            return false;
        }
    }

    public int countUsers() {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[USER COUNT ERROR] " + e.getMessage());
        }
        return 0;
    }

    // Admin operations
    public java.util.List<User> findAll() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT id, username, password, role FROM users ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("[USER LIST ERROR] " + e.getMessage());
        }
        return list;
    }

    public boolean insert(String username, String password, Role role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role.name());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updateRole(int userId, Role role) throws SQLException {
        String sql = "UPDATE users SET role=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        }
    }
}

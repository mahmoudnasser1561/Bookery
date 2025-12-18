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
}

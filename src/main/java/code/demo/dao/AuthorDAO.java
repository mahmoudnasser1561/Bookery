package code.demo.dao;

import code.demo.core.DatabaseConnection;
import code.demo.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorDAO {

    public List<Author> findAll() {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT id, name, bio, nationality FROM authors ORDER BY name";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[AUTHOR LIST ERROR] " + e.getMessage());
        }
        return list;
    }

    // Alias to match requirement wording
    public List<Author> getAllAuthors() { return findAll(); }

    public Optional<Author> findById(int id) {
        String sql = "SELECT id, name, bio, nationality FROM authors WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AUTHOR FIND ERROR] " + e.getMessage());
        }
        return Optional.empty();
    }

    public void insert(Author a) throws SQLException {
        String sql = "INSERT INTO authors (name, bio, nationality) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getBio());
            ps.setString(3, a.getNationality());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) a.setId(keys.getInt(1)); }
        }
    }

    public void update(Author a) throws SQLException {
        String sql = "UPDATE authors SET name=?, bio=?, nationality=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getBio());
            ps.setString(3, a.getNationality());
            ps.setInt(4, a.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM authors WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Author map(ResultSet rs) throws SQLException {
        return new Author(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("bio"),
                rs.getString("nationality")
        );
    }
}

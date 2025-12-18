package code.demo.dao;

import code.demo.core.DatabaseConnection;
import code.demo.model.Book;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT id, title, author, category, publish_date, isbn FROM books ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[BOOK LIST ERROR] " + e.getMessage());
        }
        return list;
    }

    public int countBooks() {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[BOOK COUNT ERROR] " + e.getMessage());
        }
        return 0;
    }

    public List<Book> search(String pattern) {
        List<Book> list = new ArrayList<>();
        String like = "%" + pattern + "%";
        String sql = "SELECT id, title, author, category, publish_date, isbn FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ? ORDER BY id DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like); ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BOOK SEARCH ERROR] " + e.getMessage());
        }
        return list;
    }

    public void insert(Book b) throws SQLException {
        String sql = "INSERT INTO books (title, author, category, publish_date, isbn) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getCategory());
            if (b.getPublishDate() != null) ps.setDate(4, Date.valueOf(b.getPublishDate())); else ps.setNull(4, Types.DATE);
            ps.setString(5, b.getIsbn());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) b.setId(keys.getInt(1)); }
        }
    }

    public void update(Book b) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, category=?, publish_date=?, isbn=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getCategory());
            if (b.getPublishDate() != null) ps.setDate(4, Date.valueOf(b.getPublishDate())); else ps.setNull(4, Types.DATE);
            ps.setString(5, b.getIsbn());
            ps.setInt(6, b.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Book map(ResultSet rs) throws SQLException {
        LocalDate date = null;
        Date d = rs.getDate("publish_date");
        if (d != null) date = d.toLocalDate();
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                date,
                rs.getString("isbn")
        );
    }
}

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
        String sql = "SELECT b.id, b.title, b.author, b.author_id, b.category, b.publish_date, b.isbn, a.name AS author_name " +
                "FROM books b LEFT JOIN authors a ON b.author_id = a.id ORDER BY b.id DESC";
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
        String sql = "SELECT b.id, b.title, b.author, b.author_id, b.category, b.publish_date, b.isbn, a.name AS author_name " +
                "FROM books b LEFT JOIN authors a ON b.author_id = a.id " +
                "WHERE b.title LIKE ? OR b.author LIKE ? OR b.category LIKE ? OR b.isbn LIKE ? OR a.name LIKE ? ORDER BY b.id DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like); ps.setString(4, like); ps.setString(5, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BOOK SEARCH ERROR] " + e.getMessage());
        }
        return list;
    }

    public void insert(Book b) throws SQLException {
        String sql = "INSERT INTO books (title, author, author_id, category, publish_date, isbn) VALUES (?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            if (b.getAuthorId() != null) ps.setInt(3, b.getAuthorId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, b.getCategory());
            if (b.getPublishDate() != null) ps.setDate(5, Date.valueOf(b.getPublishDate())); else ps.setNull(5, Types.DATE);
            ps.setString(6, b.getIsbn());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) b.setId(keys.getInt(1)); }
        }
    }

    public void update(Book b) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, author_id=?, category=?, publish_date=?, isbn=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            if (b.getAuthorId() != null) ps.setInt(3, b.getAuthorId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, b.getCategory());
            if (b.getPublishDate() != null) ps.setDate(5, Date.valueOf(b.getPublishDate())); else ps.setNull(5, Types.DATE);
            ps.setString(6, b.getIsbn());
            ps.setInt(7, b.getId());
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
        Book b = new Book(
                rs.getInt("id"),
                rs.getString("title"),
                // Prefer joined author_name; fallback to stored author string
                rs.getString("author_name") != null ? rs.getString("author_name") : rs.getString("author"),
                rs.getString("category"),
                date,
                rs.getString("isbn")
        );
        try {
            int aid = rs.getInt("author_id");
            if (!rs.wasNull()) b.setAuthorId(aid);
        } catch (SQLException ignored) { }
        return b;
    }
}

package code.demo.controller;

import code.demo.core.UserSession;
import code.demo.dao.BookDAO;
import code.demo.model.Book;
import code.demo.model.Role;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.time.LocalDate;

public class BooksController extends BaseController {
    @FXML private TableView<Book> table;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, String> colIsbn;
    @FXML private TableColumn<Book, LocalDate> colPublish;
    @FXML private TextField txtSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getTitle()));
        colAuthor.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAuthor()));
        colCategory.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getCategory()));
        colIsbn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getIsbn()));
        colPublish.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getPublishDate()));
        colId.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getId()));


        table.setItems(data);
        refresh();

        // Enable/disable edit and delete based on selection
        btnEdit.disableProperty().bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));
        btnDelete.disableProperty().bind(Bindings.isNull(table.getSelectionModel().selectedItemProperty()));

        // Role-based UI
        Role role = UserSession.getInstance().getRole();
        boolean userReadOnly = role == Role.USER;
        btnAdd.setVisible(!userReadOnly);
        btnEdit.setVisible(!userReadOnly);
        btnDelete.setVisible(!userReadOnly);

        // search listener (simple contains; regex handled by UI instruction, but prevent exceptions)
        txtSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void refresh() {
        data.setAll(bookDAO.findAll());
    }

    private void filter(String text) {
        if (text == null || text.isBlank()) { refresh(); return; }
        // Regex-based filtering in-memory
        try {
            var pattern = java.util.regex.Pattern.compile(text, java.util.regex.Pattern.CASE_INSENSITIVE);
            var all = bookDAO.findAll();
            var filtered = all.stream().filter(b ->
                    pattern.matcher(String.valueOf(b.getId())).find() ||
                    pattern.matcher(nz(b.getTitle())).find() ||
                    pattern.matcher(nz(b.getAuthor())).find() ||
                    pattern.matcher(nz(b.getCategory())).find() ||
                    pattern.matcher(nz(b.getIsbn())).find() ||
                    (b.getPublishDate() != null && pattern.matcher(b.getPublishDate().toString()).find())
            ).toList();
            data.setAll(filtered);
        } catch (Exception ex) {
            // If invalid regex, fallback to simple contains (case-insensitive)
            String q = text.toLowerCase();
            var all = bookDAO.findAll();
            var filtered = all.stream().filter(b ->
                    String.valueOf(b.getId()).contains(q) ||
                    nz(b.getTitle()).toLowerCase().contains(q) ||
                    nz(b.getAuthor()).toLowerCase().contains(q) ||
                    nz(b.getCategory()).toLowerCase().contains(q) ||
                    nz(b.getIsbn()).toLowerCase().contains(q) ||
                    (b.getPublishDate() != null && b.getPublishDate().toString().contains(q))
            ).toList();
            data.setAll(filtered);
        }
    }

    private String nz(String s) { return s == null ? "" : s; }

    @FXML
    public void onAdd(ActionEvent e) {
        Dialog<Book> dialog = bookDialog(null);
        dialog.showAndWait().ifPresent(b -> {
            try { bookDAO.insert(b); refresh(); }
            catch (SQLException ex) { showError("Add Book Error", friendly(ex)); }
        });
    }

    @FXML
    public void onEdit(ActionEvent e) {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Dialog<Book> dialog = bookDialog(selected);
        dialog.showAndWait().ifPresent(b -> {
            try { bookDAO.update(b); refresh(); }
            catch (SQLException ex) { showError("Update Book Error", friendly(ex)); }
        });
    }

    @FXML
    public void onDelete(ActionEvent e) {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected book?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try { bookDAO.delete(selected.getId()); refresh(); }
                catch (SQLException ex) { showError("Delete Book Error", friendly(ex)); }
            }
        });
    }

    private Dialog<Book> bookDialog(Book original) {
        boolean isEdit = original != null;
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Book" : "Add Book");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField title = new TextField(isEdit ? original.getTitle() : "");
        title.setPromptText("Title");
        TextField author = new TextField(isEdit ? original.getAuthor() : "");
        author.setPromptText("Author");
        ComboBox<String> category = new ComboBox<>();
        category.getItems().addAll("Fiction","Non-Fiction","Science","History","Technology","Children");
        if (isEdit) category.getSelectionModel().select(original.getCategory());
        DatePicker publish = new DatePicker(isEdit ? original.getPublishDate() : null);
        TextField isbn = new TextField(isEdit ? original.getIsbn() : "");
        isbn.setPromptText("ISBN");

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("Title"), title);
        gp.addRow(1, new Label("Author"), author);
        gp.addRow(2, new Label("Category"), category);
        gp.addRow(3, new Label("Publish Date"), publish);
        gp.addRow(4, new Label("ISBN"), isbn);
        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (title.getText().isBlank() || author.getText().isBlank() || category.getValue() == null) {
                    showError("Validation", "Title, Author, and Category are required.");
                    return null;
                }
                if (!isbn.getText().isBlank() && !isbnPattern.matcher(isbn.getText().trim()).matches()) {
                    showError("Invalid ISBN", "Please enter a valid ISBN-10 or ISBN-13.");
                    return null;
                }
                Book b = isEdit ? original : new Book();
                b.setTitle(title.getText().trim());
                b.setAuthor(author.getText().trim());
                b.setCategory(category.getValue());
                b.setPublishDate(publish.getValue());
                b.setIsbn(isbn.getText().trim());
                return b;
            }
            return null;
        });
        return dialog;
    }

    private String friendly(SQLException ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.contains("Duplicate")) return "Duplicate value (check ISBN).";
        return msg != null ? msg : "Database error.";
    }

    // Sidebar navigation
    @FXML public void goDashboard(MouseEvent e){ switchScene(table, "view/dashboard.fxml"); }
    @FXML public void goBooks(MouseEvent e){ /* already here */ }
    @FXML public void goSettings(MouseEvent e){ switchScene(table, "view/settings.fxml"); }
    @FXML public void goAbout(MouseEvent e){ switchScene(table, "view/about.fxml"); }
}

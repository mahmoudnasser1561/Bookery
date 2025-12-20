package code.demo.controller;

import code.demo.dao.AuthorDAO;
import code.demo.model.Author;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;

public class AuthorManagementController extends BaseController {

    @FXML private TableView<Author> table;
    @FXML private TableColumn<Author, Number> colId;
    @FXML private TableColumn<Author, String> colName;
    @FXML private TableColumn<Author, String> colNationality;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final AuthorDAO authorDAO = new AuthorDAO();
    private final ObservableList<Author> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (!isAdmin()) {
            showError("Access Denied", "Admins only.");
            if (table != null) switchScene(table, "view/dashboard.fxml");
            return;
        }
        colId.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getId()));
        colName.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getName()));
        colNationality.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNationality()));
        table.setItems(data);
        refresh();
        btnEdit.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
    }

    private void refresh() { data.setAll(authorDAO.findAll()); }

    @FXML public void onAdd(ActionEvent e) { showDialog(null); }
    @FXML public void onEdit(ActionEvent e) { showDialog(table.getSelectionModel().getSelectedItem()); }
    @FXML public void onDelete(ActionEvent e) {
        Author a = table.getSelectionModel().getSelectedItem();
        if (a == null) return;
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected author?", ButtonType.OK, ButtonType.CANCEL);
        conf.setHeaderText(null);
        conf.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try { authorDAO.delete(a.getId()); refresh(); }
                catch (SQLException ex) { showError("Delete Author Error", ex.getMessage()); }
            }
        });
    }

    private void showDialog(Author original) {
        boolean edit = original != null;
        Dialog<Author> dlg = new Dialog<>();
        dlg.setTitle(edit ? "Edit Author" : "Add Author");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField name = new TextField(edit ? original.getName() : ""); name.setPromptText("Name");
        TextField nationality = new TextField(edit ? original.getNationality() : ""); nationality.setPromptText("Nationality");
        TextArea bio = new TextArea(edit ? original.getBio() : ""); bio.setPromptText("Bio"); bio.setPrefRowCount(4);
        GridPane gp = new GridPane(); gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("Name"), name);
        gp.addRow(1, new Label("Nationality"), nationality);
        gp.add(new Label("Bio"), 0, 2); gp.add(bio, 1, 2);
        dlg.getDialogPane().setContent(gp);
        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                if (name.getText().isBlank()) { showError("Validation", "Name is required."); return null; }
                Author a = edit ? original : new Author();
                a.setName(name.getText().trim());
                a.setNationality(nationality.getText().trim());
                a.setBio(bio.getText().trim());
                return a;
            }
            return null;
        });
        dlg.showAndWait().ifPresent(a -> {
            try {
                if (edit) authorDAO.update(a); else authorDAO.insert(a);
                refresh();
            } catch (SQLException ex) { showError(edit?"Update Author Error":"Add Author Error", ex.getMessage()); }
        });
    }

    // Sidebar navigation
    @FXML public void goDashboard(MouseEvent e){ switchScene(table, "view/dashboard.fxml"); }
    @FXML public void goBooks(MouseEvent e){ switchScene(table, "view/books.fxml"); }
    @FXML public void goUsers(MouseEvent e){ switchScene(table, "view/user_management.fxml"); }
    @FXML public void goAuthors(MouseEvent e){ /* already here */ }
    @FXML public void goSettings(MouseEvent e){ switchScene(table, "view/settings.fxml"); }
    @FXML public void goAbout(MouseEvent e){ switchScene(table, "view/about.fxml"); }
}

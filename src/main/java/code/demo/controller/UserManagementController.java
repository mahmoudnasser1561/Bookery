package code.demo.controller;

import code.demo.core.UserSession;
import code.demo.dao.UserDAO;
import code.demo.model.Role;
import code.demo.model.User;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;

public class UserManagementController extends BaseController {

    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private Button btnAdd;
    @FXML private Button btnEditRole;
    @FXML private Button btnDelete;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (!isAdmin()) {
            showError("Access Denied", "Admins only.");
            // route back to dashboard
            if (table != null) switchScene(table, "view/dashboard.fxml");
            return;
        }

        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colUsername.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getUsername()));
        colRole.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getRole().name()));
        table.setItems(data);
        refresh();

        btnEditRole.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
    }

    private void refresh() {
        data.setAll(userDAO.findAll());
    }

    @FXML
    public void onAdd(ActionEvent e) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField email = new TextField(); email.setPromptText("Email");
        PasswordField pass = new PasswordField(); pass.setPromptText("Password (min 6)");
        ComboBox<Role> role = new ComboBox<>(); role.getItems().setAll(Role.values()); role.getSelectionModel().select(Role.USER);

        GridPane gp = new GridPane(); gp.setHgap(8); gp.setVgap(8);
        gp.addRow(0, new Label("Email"), email);
        gp.addRow(1, new Label("Password"), pass);
        gp.addRow(2, new Label("Role"), role);
        dialog.getDialogPane().setContent(gp);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String em = email.getText().trim();
                String pw = pass.getText();
                if (!emailPattern.matcher(em).matches() || pw == null || pw.length() < 6) {
                    showError("Validation", "Valid email and password (min 6) required.");
                    return null;
                }
                User u = new User();
                u.setUsername(em); u.setPassword(pw); u.setRole(role.getValue());
                return u;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            try {
                userDAO.insert(u.getUsername(), u.getPassword(), u.getRole());
                refresh();
            } catch (SQLException ex) {
                showError("Add User Error", ex.getMessage());
            }
        });
    }

    @FXML
    public void onEditRole(ActionEvent e) {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        ChoiceDialog<Role> dlg = new ChoiceDialog<>(selected.getRole(), Role.values());
        dlg.setTitle("Edit Role"); dlg.setHeaderText(null); dlg.setContentText("Select role:");
        dlg.showAndWait().ifPresent(r -> {
            try {
                userDAO.updateRole(selected.getId(), r);
                refresh();
            } catch (SQLException ex) {
                showError("Update Role Error", ex.getMessage());
            }
        });
    }

    @FXML
    public void onDelete(ActionEvent e) {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected user?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try { userDAO.delete(selected.getId()); refresh(); }
                catch (SQLException ex) { showError("Delete User Error", ex.getMessage()); }
            }
        });
    }

    // Sidebar navigation
    @FXML public void goDashboard(MouseEvent e){ switchScene(table, "view/dashboard.fxml"); }
    @FXML public void goBooks(MouseEvent e){ switchScene(table, "view/books.fxml"); }
    @FXML public void goUsers(MouseEvent e){ /* already here */ }
    @FXML public void goAuthors(MouseEvent e){ switchScene(table, "view/author_management.fxml"); }
    @FXML public void goSettings(MouseEvent e){ switchScene(table, "view/settings.fxml"); }
    @FXML public void goAbout(MouseEvent e){ switchScene(table, "view/about.fxml"); }
}

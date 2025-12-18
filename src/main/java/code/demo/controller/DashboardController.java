package code.demo.controller;

import code.demo.core.DatabaseConnection;
import code.demo.core.UserSession;
import code.demo.dao.BookDAO;
import code.demo.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class DashboardController extends BaseController {
    @FXML private Label lblWelcome;
    @FXML private Label lblBooks;
    @FXML private Label lblUsers;
    @FXML private Label lblDbStatus;

    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        lblWelcome.setText("Welcome, " + UserSession.getInstance().getUsername());
        lblBooks.setText(String.valueOf(bookDAO.countBooks()));
        lblUsers.setText(String.valueOf(userDAO.countUsers()));
        lblDbStatus.setText(DatabaseConnection.getInstance().testConnection() ? "Online" : "Offline");
    }

    // Sidebar navigation
    @FXML public void goDashboard(MouseEvent e){ /* already here */ }
    @FXML public void goBooks(MouseEvent e){ switchScene(lblWelcome, "view/books.fxml"); }
    @FXML public void goSettings(MouseEvent e){ switchScene(lblWelcome, "view/settings.fxml"); }
    @FXML public void goAbout(MouseEvent e){ switchScene(lblWelcome, "view/about.fxml"); }
}

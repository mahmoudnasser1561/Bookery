package code.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class AboutController extends BaseController {
    @FXML private Label lblTitle;

    @FXML
    public void initialize() {
        // Nothing dynamic for now
    }

    // Sidebar navigation
    @FXML public void goDashboard(MouseEvent e){ switchScene(lblTitle, "view/dashboard.fxml"); }
    @FXML public void goBooks(MouseEvent e){ switchScene(lblTitle, "view/books.fxml"); }
    @FXML public void goSettings(MouseEvent e){ switchScene(lblTitle, "view/settings.fxml"); }
    @FXML public void goAbout(MouseEvent e){ /* already here */ }
}

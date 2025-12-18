package code.demo.controller;

import code.demo.core.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

public class SettingsController extends BaseController {
    @FXML private Slider fontSlider;
    @FXML private Label lblFontValue;
    @FXML private Button btnTestDb;
    @FXML private Button btnLight;
    @FXML private Button btnDark;

    @FXML
    public void initialize() {
        fontSlider.setMin(10);
        fontSlider.setMax(22);
        fontSlider.setValue(14);
        lblFontValue.setText(((int)fontSlider.getValue()) + "px");
        fontSlider.valueProperty().addListener((obs, o, n) -> {
            lblFontValue.setText(n.intValue() + "px");
            Scene scene = btnTestDb.getScene();
            scene.getRoot().setStyle("-fx-font-size: " + n.intValue() + "px;");
        });
    }

    @FXML
    public void onTestDb() {
        boolean ok = DatabaseConnection.getInstance().testConnection();
        if (ok) showInfo("Database", "Connection OK"); else showError("Database", "Connection failed");
    }

    @FXML public void onLight() { setTheme(btnLight.getScene(), false); }
    @FXML public void onDark() { setTheme(btnDark.getScene(), true); }

    // Sidebar navigation
    @FXML public void goDashboard(MouseEvent e){ switchScene(btnTestDb, "view/dashboard.fxml"); }
    @FXML public void goBooks(MouseEvent e){ switchScene(btnTestDb, "view/books.fxml"); }
    @FXML public void goSettings(MouseEvent e){ /* already here */ }
    @FXML public void goAbout(MouseEvent e){ switchScene(btnTestDb, "view/about.fxml"); }
}

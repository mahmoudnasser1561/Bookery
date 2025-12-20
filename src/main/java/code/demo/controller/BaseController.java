package code.demo.controller;

import code.demo.MainApplication;
import code.demo.core.UserSession;
import code.demo.model.Role;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class BaseController {

    protected final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    protected final Pattern isbnPattern = Pattern.compile("^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$|^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

    protected void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    protected void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    protected void switchScene(Node anyNodeInScene, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath));
            Parent newContent = loader.load();
            Scene scene = anyNodeInScene.getScene();
            if (scene.getRoot() instanceof StackPane sp) {
                // Preserve the global overlay (last child) and replace only the content (first child)
                if (sp.getChildren().isEmpty()) {
                    sp.getChildren().add(newContent);
                } else {
                    // assume index 0 is content, others (e.g., overlay) stay
                    sp.getChildren().set(0, newContent);
                }
            } else {
                scene.setRoot(newContent);
            }
        } catch (IOException e) {
            showError("Navigation Error", e.getMessage());
        }
    }

    protected boolean isAdmin() {
        return UserSession.getInstance().getRole() == Role.ADMIN;
    }

    public void setTheme(Scene scene, boolean dark) {
        String darkUrl = Objects.requireNonNull(MainApplication.class.getResource("css/dark.css")).toExternalForm();
        String lightUrl = Objects.requireNonNull(MainApplication.class.getResource("css/light.css")).toExternalForm();
        scene.getStylesheets().removeAll(darkUrl, lightUrl);
        scene.getStylesheets().add(dark ? darkUrl : lightUrl);
    }
}

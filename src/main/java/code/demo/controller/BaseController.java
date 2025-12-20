package code.demo.controller;

import code.demo.MainApplication;
import code.demo.core.UserSession;
import code.demo.model.Role;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
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
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setTitle(title);
        a.setHeaderText(null);

        // --- (نفس كود الصورة السابق) ---
        ImageView imageView = null;
        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream("/error.png");
            if (imageStream != null) {
                Image image = new Image(imageStream);
                imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(300);
            }
        } catch (Exception e) { e.printStackTrace(); }

        // --- (نفس كود النص السابق) ---
        Label label = new Label(msg);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // --- (التعديل المهم هنا) ---
        Button okBtn = new Button("OK");
        okBtn.setMinWidth(80);

        // 1. إصلاح زر الـ OK الخاص بنا
        // (يجب تعيين النتيجة ليعرف التنبيه أنه انتهى)
        okBtn.setOnAction(e -> {
            a.setResult(javafx.scene.control.ButtonType.OK);
            a.close();
        });

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        if (imageView != null) vbox.getChildren().add(imageView);
        vbox.getChildren().addAll(label, okBtn);

        a.getDialogPane().setContent(vbox);

        // 2. إصلاح زر الإغلاق (X) الموجود في الشريط العلوي
        // نحصل على الـ Window الخاصة بالتنبيه ونجبرها على العمل
        javafx.stage.Window window = a.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> a.close());

        a.showAndWait();
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

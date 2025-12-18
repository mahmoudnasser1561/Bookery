package code.demo;

import code.demo.core.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConnection.getInstance().initialize();

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("view/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 960, 640);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css/app.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css/light.css")).toExternalForm());

        stage.setTitle("Library Management System");
        try {
            Image icon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png")));
            stage.getIcons().add(icon);
        } catch (Exception ignored) { }
        stage.setScene(scene);
        stage.show();
    }
}

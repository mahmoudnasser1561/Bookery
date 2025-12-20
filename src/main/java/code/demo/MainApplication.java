package code.demo;

import code.demo.core.ConnectivityMonitor;
import code.demo.core.DatabaseConnection;
import code.demo.ui.ConnectionOverlay;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    public static final String OVERLAY_NODE_ID = "connectionOverlay";

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConnection.getInstance().initialize();

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("view/login.fxml"));
        Parent contentRoot = loader.load();

        // Wrap content in a StackPane to host a global connection overlay
        StackPane rootContainer = new StackPane(contentRoot);

        // Create and attach overlay bound to connectivity monitor
        var overlay = ConnectionOverlay.create();
        overlay.setId(OVERLAY_NODE_ID);
        overlay.visibleProperty().bind(ConnectivityMonitor.getInstance().connectedProperty().not());
        // Block interactions of entire container while overlay visible
        rootContainer.disableProperty().bind(overlay.visibleProperty());
        rootContainer.getChildren().add(overlay);

        Scene scene = new Scene(rootContainer, 960, 640);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css/app.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css/light.css")).toExternalForm());

        // Start the background monitor
//        ConnectivityMonitor.getInstance().start();

        stage.setTitle("Library Management System");
        try {
            Image icon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon.png")));
            stage.getIcons().add(icon);
        } catch (Exception ignored) { }
        stage.setScene(scene);
        stage.show();
    }
}

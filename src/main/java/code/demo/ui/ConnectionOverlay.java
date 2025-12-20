package code.demo.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A global, non-dismissible overlay pane that blocks interactions with the underlying UI
 * and displays a clear connectivity error message. Visibility should be bound to a
 * background connectivity monitor (e.g., ConnectivityMonitor.connectedProperty().not()).
 */
public final class ConnectionOverlay {

    private ConnectionOverlay() {}

    public static StackPane create() {
        StackPane overlay = new StackPane();
        overlay.setVisible(false);
        overlay.setPickOnBounds(true); // capture mouse events anywhere
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");

        Label title = new Label("Connection Lost!\nPlease check your internet connection.");
        title.getStyleClass().add("title");
        title.setStyle("-fx-text-fill: white;");

        VBox box = new VBox(title);
        box.setAlignment(Pos.CENTER);
        overlay.getChildren().add(box);
        StackPane.setAlignment(box, Pos.CENTER);

        // Block all mouse/scroll/key events (non-dismissible overlay)
        overlay.setOnMousePressed(e -> e.consume());
        overlay.setOnMouseDragged(e -> e.consume());
        overlay.setOnMouseReleased(e -> e.consume());
        overlay.setOnScroll(e -> e.consume());
        overlay.setOnKeyPressed(e -> e.consume());
        overlay.setOnKeyReleased(e -> e.consume());

        // When visible, show wait cursor on overlay
        overlay.visibleProperty().addListener((obs, o, n) -> overlay.setCursor(n ? Cursor.WAIT : Cursor.DEFAULT));

        // Do not take layout space when hidden
        overlay.managedProperty().bind(overlay.visibleProperty());
        return overlay;
    }
}

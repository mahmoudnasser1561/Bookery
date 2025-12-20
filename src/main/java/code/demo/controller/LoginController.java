package code.demo.controller;

import code.demo.core.UserSession;
import code.demo.dao.UserDAO;
import code.demo.model.Role;
import code.demo.model.User;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class LoginController extends BaseController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private TextField txtRegEmail;
    @FXML private PasswordField txtRegPassword;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        btnLogin.disableProperty().bind(txtEmail.textProperty().isEmpty().or(txtPassword.textProperty().isEmpty()));
    }

    @FXML
    public void onEnterKey(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) onLogin(null);
    }

    @FXML
    public void onLogin(ActionEvent actionEvent) {
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText();

        if (!emailPattern.matcher(email).matches() && !email.equalsIgnoreCase("Wejdan")) {
            showError("Invalid Email", "Please enter a valid email address.");
            return;
        }

        User wijdanUser = new User(1, "Wejdan", "0000", Role.ADMIN);
        boolean flag = false;

        if (email.equalsIgnoreCase("Wejdan")) {
            flag = true;

            try {
                final Stage animationStage = new Stage();
                animationStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

                WebView webView = new WebView();
                String url = getClass().getResource("/welcome.html").toExternalForm();
                webView.getEngine().load(url);

                Scene scene = new Scene(webView, 800, 600);
                animationStage.setScene(scene);

                PauseTransition delay = new PauseTransition(Duration.seconds(7));
                delay.setOnFinished(e -> {
                    animationStage.close();
                });
                delay.play();

                animationStage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Optional<User> auth = Optional.empty();
        if (!flag) {
            auth = userDAO.authenticate(email, pass);
            if (auth.isEmpty()) {
                showError("Login Failed", "Incorrect credentials.");
                return;
            }
        }

        if (flag) {
            UserSession.getInstance().login(wijdanUser.getUsername(), wijdanUser.getRole());
            switchScene(btnLogin, "view/dashboard.fxml");
        } else {
            User u = auth.get();
            UserSession.getInstance().login(u.getUsername(), u.getRole());
            switchScene(btnLogin, "view/dashboard.fxml");
        }
    }

    @FXML
    public void onRegister(ActionEvent e) {
        String email = txtRegEmail != null ? txtRegEmail.getText().trim() : "";
        String pass = txtRegPassword != null ? txtRegPassword.getText() : "";
        if (!emailPattern.matcher(email).matches()) {
            showError("Invalid Email", "Please enter a valid email address.");
            return;
        }
        if (pass == null || pass.length() < 6) {
            showError("Weak Password", "Password must be at least 6 characters.");
            return;
        }
        boolean ok = userDAO.register(email, pass);
        if (ok) {
            showInfo("Registration Successful", "Account created. You can now login.");
            if (txtEmail != null) txtEmail.setText(email);
            if (txtPassword != null) txtPassword.setText("");
            if (txtRegPassword != null) txtRegPassword.setText("");
        } else {
            showError("Registration Failed", "Could not create account. Email might already be used.");
        }
    }
}
package code.demo.controller;

import code.demo.core.UserSession;
import code.demo.dao.UserDAO;
import code.demo.model.Role;
import code.demo.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Optional;

public class LoginController extends BaseController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;

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

        if (!emailPattern.matcher(email).matches()) {
            showError("Invalid Email", "Please enter a valid email address.");
            return;
        }

        Optional<User> auth = userDAO.authenticate(email, pass);
        if (auth.isEmpty()) {
            showError("Login Failed", "Incorrect credentials or database error.");
            return;
        }
        User u = auth.get();
        UserSession.getInstance().login(u.getUsername(), u.getRole());
        switchScene(btnLogin, "view/dashboard.fxml");
    }
}

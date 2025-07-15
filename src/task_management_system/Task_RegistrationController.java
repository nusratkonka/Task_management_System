package task_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class Task_RegistrationController implements Initializable {

    @FXML private TextField tf3;  // username
    @FXML private TextField tf4;  // phone
    @FXML private TextField tf5;  // password
    @FXML private Button btn3;    // sign‑up
    @FXML private Button btn4;    // back to login

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void signup(ActionEvent event) {
        String username = tf3.getText().trim();
        String phone    = tf4.getText().trim();
        String password = tf5.getText().trim();

        if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }
        if (!phone.matches("\\d{11}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone", "Phone number must be exactly 11 digits.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM users WHERE username = ?");
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                showAlert(Alert.AlertType.WARNING, "Taken", "Username already exists. Try another.");
                return;
            }

            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO users (username, phone, password) VALUES (?,?,?)");
            insert.setString(1, username);
            insert.setString(2, phone);
            insert.setString(3, password);      
            insert.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success",
                      "Account created successfully! You can now log in.");

            tf3.clear(); tf4.clear(); tf5.clear();
            loadLogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error", "Database problem occurred.");
        }
    }

    @FXML
    private void signin_frm_reg(ActionEvent event) {
        loadLogin(event);
    }


    private void loadLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Task_login.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Management - Login");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open login page.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

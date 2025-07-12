package task_management_system;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Task_RegistrationController implements Initializable {

    @FXML private TextField tf3; 
    @FXML private TextField tf4; 
    @FXML private TextField tf5; 
    @FXML private Button btn3;   
    @FXML private Button btn4; 

    public static String registeredUsername = "";
    public static String registeredPassword = "";
    public static String registeredPhone = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void signup(ActionEvent event) {
        String username = tf3.getText().trim();
        String phone = tf4.getText().trim();
        String password = tf5.getText().trim();

        if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        if (!phone.matches("\\d{11}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Phone number must be exactly 11 digits.");
            return;
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredPhone = phone;

        showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");

      
        tf3.clear();
        tf4.clear();
        tf5.clear();

      
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Task_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Management - Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page.");
        }
    }

    @FXML
    private void signin_frm_reg(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Task_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Management - Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

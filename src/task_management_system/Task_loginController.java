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

public class Task_loginController implements Initializable {

    @FXML private TextField tf1; // username
    @FXML private TextField tf2; // password
    @FXML private Button btn1;   // login
    @FXML private Button btn2;   // create account

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization if needed
    }

    @FXML
    private void signin_1(ActionEvent event) {
        String inputUsername = tf1.getText().trim();
        String inputPassword = tf2.getText().trim();

        if (inputUsername.equals(Task_RegistrationController.registeredUsername)
                && inputPassword.equals(Task_RegistrationController.registeredPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + inputUsername + "!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
        }
    }

    @FXML
    private void createA_1(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Task_Registration.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Management - Registration");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load registration page.");
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

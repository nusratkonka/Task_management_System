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
import javafx.scene.Node;
import javafx.stage.Stage;

public class Task_loginController implements Initializable {

    @FXML private TextField tf1; // username
    @FXML private TextField tf2; // password 
    @FXML private Button   btn1; // login
    @FXML private Button   btn2; // create account

    @Override
    public void initialize(URL url, ResourceBundle rb) { }
    @FXML
    private void signin_1(ActionEvent event) {
        String inputUsername = tf1.getText().trim();
        String inputPassword = tf2.getText().trim();

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Field",
                      "Please enter both username and password.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, inputUsername);
            stmt.setString(2, inputPassword); 

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                          "Welcome, " + inputUsername + "!");

                loadDashboard(event, inputUsername);

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed",
                          "Incorrect username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error",
                      "Could not connect to database.");
        }

        tf1.clear();
        tf2.clear();
    }

    @FXML
    private void createA_1(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Task_Registration.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Management - Registration");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                      "Failed to load registration page.");
        }
    }
    private void loadDashboard(ActionEvent event, String loggedUserName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent     root   = loader.load();
            DashboardController dash = loader.getController();
            dash.setUserName(loggedUserName);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Management - Dashboard");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                      "Failed to open dashboard.");
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

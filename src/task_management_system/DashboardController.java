package task_management_system;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DashboardController implements Initializable {
    @FXML private ListView<Task> listview;
    @FXML private TextField text1;       // Task headline
    @FXML private TextField textdetails; // Task description
    @FXML private Button btn4; // Add
    @FXML private Button btn5; // Update
    @FXML private Button btn6; // Delete
    @FXML private Button btn7; // Incomplete
    @FXML private Button btn8; // Complete
    @FXML private Button btn9; // All Tasks
    @FXML private Label  name_label;     // Logged‑in user's name
    private final ObservableList<Task> masterList = FXCollections.observableArrayList();
    private ObservableList<Task> filteredList;
    private Task selectedTask = null;

    private String currentUserName;
    @FXML
    private Button btnlogout;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filteredList = FXCollections.observableArrayList(masterList);
        listview.setItems(filteredList);

        listview.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedTask = newV;
            btn5.setDisable(newV == null);
            btn6.setDisable(newV == null);

            if (newV != null) {
                text1.setText(newV.getName());
                textdetails.setText(newV.getDescription());
            } else {
                text1.clear();
                textdetails.clear();
            }
        });

        btn5.setDisable(true);
        btn6.setDisable(true);
    }

    public void setUserName(String userName) throws Exception {
        this.currentUserName = userName;
        name_label.setText(userName);

        loadTasksFromDB();  
    }

    private void loadTasksFromDB() throws Exception {
        masterList.clear();
        String sql = "SELECT * FROM tasks WHERE username = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUserName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int    id   = rs.getInt("id");
                String name = rs.getString("title");
                String des  = rs.getString("description");
                LocalDateTime dt = rs.getTimestamp("created_at").toInstant()
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDateTime();
                boolean done = rs.getBoolean("done");

                masterList.add(new Task(id, name, des, dt, done));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error",
                      "Could not load tasks from database.");
        }
        refreshFilter();
    }

    @FXML
    private void add(ActionEvent event) throws Exception {
        String taskName        = text1.getText().trim();
        String taskDescription = textdetails.getText().trim();

        if (taskName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Task", "Please enter a task headline.");
            return;
        }

        String sql = "INSERT INTO tasks (username, title, description) VALUES (?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, currentUserName);
            stmt.setString(2, taskName);
            stmt.setString(3, taskDescription);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                Task t = new Task(id, taskName, taskDescription,
                                  LocalDateTime.now(), false);
                masterList.add(0, t); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error", "Could not add task.");
        }

        refreshFilter();
        text1.clear();
        textdetails.clear();
    }

    @FXML
    private void update(ActionEvent event) throws Exception {
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a task to update.");
            return;
        }
        String newName        = text1.getText().trim();
        String newDescription = textdetails.getText().trim();

        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Task", "Task headline cannot be empty.");
            return;
        }

        String sql = "UPDATE tasks SET title = ?, description = ? WHERE id = ? AND username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, newDescription);
            stmt.setInt(3, selectedTask.getId());
            stmt.setString(4, currentUserName);
            stmt.executeUpdate();
            selectedTask.setName(newName);
            selectedTask.setDescription(newDescription);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error", "Could not update task.");
        }

        refreshFilter();
        text1.clear();
        textdetails.clear();
        listview.getSelectionModel().clearSelection();
    }

    @FXML
    private void delete(ActionEvent event) {
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a task to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText("Are you sure you want to delete the task?");
        confirm.setContentText(selectedTask.toString());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                String sql = "DELETE FROM tasks WHERE id = ? AND username = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, selectedTask.getId());
                    stmt.setString(2, currentUserName);
                    stmt.executeUpdate();

                    masterList.remove(selectedTask);

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "DB Error", "Could not delete task.");
                } catch (Exception ex) {
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                }

                refreshFilter();
                text1.clear();
                textdetails.clear();
                listview.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML private void incomplete(ActionEvent event) { filteredList.setAll(masterList.filtered(t -> !t.isDone())); }
    @FXML private void complete  (ActionEvent event) { filteredList.setAll(masterList.filtered(Task::isDone)); }
    @FXML private void alltask   (ActionEvent event) { filteredList.setAll(masterList); }

  
    @FXML
    private void onListClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Task t = listview.getSelectionModel().getSelectedItem();
            if (t == null) return;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Task Details");
            alert.setHeaderText(t.getName());

            String details = "Description:\n" + (t.getDescription().isEmpty() ? "No description." : t.getDescription()) +
                             "\n\nCreated at: " + t.getDatetime() +
                             (t.isDone() ? "\n\nStatus: Done" : "\n\nStatus: Not Done");

            alert.setContentText(details);

            ButtonType doneBtn   = new ButtonType("Mark as Done");
            ButtonType notDoneBtn= new ButtonType("Mark as Not Done");
            ButtonType closeBtn  = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(doneBtn, notDoneBtn, closeBtn);

            alert.showAndWait().ifPresent(response -> {
                if (response == doneBtn) {
                    try {
                        updateDoneStatus(t, true);
                    } catch (Exception ex) {
                        Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (response == notDoneBtn) {
                    try {
                        updateDoneStatus(t, false);
                    } catch (Exception ex) {
                        Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }

    private void updateDoneStatus(Task task, boolean done) throws Exception {
        String sql = "UPDATE tasks SET done = ? WHERE id = ? AND username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, done);
            stmt.setInt(2, task.getId());
            stmt.setString(3, currentUserName);
            stmt.executeUpdate();

            task.setDone(done);
            refreshFilter();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error", "Could not change status.");
        }
    }

    private void refreshFilter() { filteredList.setAll(listview.getItems().equals(masterList)
                                        ? masterList
                                        : listview.getItems()); }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
private void logout(ActionEvent event) {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Logout Confirmation");
    confirm.setHeaderText("Are you sure you want to logout?");
    confirm.setContentText("You will be returned to the login screen.");

    confirm.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            try {
                // Login FXML
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("Task_login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource())
                                        .getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("Task Management - Login");
                stage.show(); 
                masterList.clear(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Error");
                err.setHeaderText(null);
                err.setContentText("Could not load login screen.");
                err.showAndWait();
            }
        }
    });
}

}

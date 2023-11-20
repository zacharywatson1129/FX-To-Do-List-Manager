package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class UIDesignController implements Initializable {

    Connection connection;

    @FXML
    private TextField textField;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnToggle;

    @FXML
    private ChoiceBox<String> choiceBox;
    ObservableList<String> choiceList;

    @FXML
    private void addItem() {
        if (textField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Sorry, please provide a description for this to-do item.");
            Optional<ButtonType> o = alert.showAndWait();
            if (o.equals(ButtonType.OK)) {
                alert.close();
            } else {
                alert.close();
            }
        }
        else {
            try {
                String description = textField.getText();
                String priority = choiceBox.getValue();
                String status = "Not complete";
                textField.setText(null);
                list.add(new ToDoItem(status, description, priority));
                String updateQuery = "INSERT INTO List(Description,Status,Priority) VALUES (?,?,?)";
                PreparedStatement ps = connection.prepareStatement(updateQuery);
                ps.setString(1, description);
                ps.setString(2, status);
                ps.setString(3, priority);
                ps.executeUpdate();
                list.clear();
                String dataQuery = "Select Id, Description, Status, Priority FROM List";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(dataQuery);
                while (rs.next()) {
                	int id = rs.getInt("Id");
                    String description1 = rs.getString("Description");
                    String status1 = rs.getString("Status");
                    String priority1 = rs.getString("Priority");
                    list.add(new ToDoItem(id, status1, description1, priority1));
                }
                tableView.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteItem() {
		try {
			if (tableView.getSelectionModel().getSelectedItem() != null) {
				ToDoItem selectedItem = tableView.getSelectionModel().getSelectedItem();
				int id = selectedItem.getId();
				String deleteQuery = "DELETE FROM List WHERE Id = ?";
				PreparedStatement ps = connection.prepareStatement(deleteQuery);
				ps.setInt(1, id);
				ps.executeUpdate();
				list.clear();
				tableView.refresh();
				// After clearing the data, this re-retrieves the data from the SQLite Database...
				String dataQuery = "Select Id, Description, Status, Priority FROM List";
	            Statement statement = connection.createStatement();
	            ResultSet rs = statement.executeQuery(dataQuery);
	            while (rs.next()) {
	            	int id1 = rs.getInt("Id");
	            	System.out.println(id1);
	                String description = rs.getString("Description");
	                String status = rs.getString("Status");
	                String priority = rs.getString("Priority");
	                list.add(new ToDoItem(id1, status, description, priority));
	            }
	            tableView.refresh();
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Sorry, please select an item to delete it.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    @FXML
    private void toggleStatus() {
    	try {
    		System.out.println("The event was fired, at least!");
    		if (tableView.getSelectionModel().getSelectedItem() != null) {
    			ToDoItem selectedItem = tableView.getSelectionModel().getSelectedItem();
    			int id = selectedItem.getId();
    			if (selectedItem.getStatus().equals("Not complete")) {
    				System.out.println("The error must not be too bad...");
    				String updateQuery = "UPDATE List SET Status = 'Partially complete' WHERE Id = ?";
	    			PreparedStatement ps = connection.prepareStatement(updateQuery);
	    			ps.setInt(1, id);
	    			ps.executeUpdate();
    			} else if (selectedItem.getStatus().equals("Partially complete")) {
    				System.out.println(selectedItem.getStatus());
    				String updateQuery = "UPDATE List SET Status = 'Complete' WHERE Id = ?";
	    			PreparedStatement ps = connection.prepareStatement(updateQuery);
	    			ps.setInt(1, id);
	    			ps.executeUpdate();
    			} else if (selectedItem.getStatus().equals("Complete")) {
    				System.out.println(selectedItem.getStatus());
    				String updateQuery = "UPDATE List SET Status = 'Not complete' WHERE Id = ?";
	    			PreparedStatement ps = connection.prepareStatement(updateQuery);
	    			ps.setInt(1, id);
	    			ps.executeUpdate();
    			} else {
    				System.out.println("I have no clue what's going on...");
    			}
    			list.clear();
    			if (list.isEmpty()) 
					System.out.println("Sucess");
				tableView.refresh();
				// After clearing the data, this re-retrieves the data from the SQLite Database...
				String dataQuery = "Select Id, Description, Status, Priority FROM List";
	            Statement statement = connection.createStatement();
	            ResultSet rs = statement.executeQuery(dataQuery);
	            while (rs.next()) {
	            	int id1 = rs.getInt("Id");
	            	System.out.println(id1);
	                String description = rs.getString("Description");
	                String status = rs.getString("Status");
	                String priority = rs.getString("Priority");
	                list.add(new ToDoItem(id1, status, description, priority));
	            }
	            tableView.refresh();
    		} else {
    			Alert alert = new Alert(AlertType.ERROR);
    			Optional<ButtonType> result = alert.showAndWait();
    			if (result.get() == ButtonType.OK) {
    				alert.close();
    			} else {
    				alert.close();
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    @FXML
    private TableView<ToDoItem> tableView;

    @FXML
    private TableColumn<ToDoItem, String> statusColumn;

    @FXML
    private TableColumn<ToDoItem, String> descriptionColumn;

    @FXML
    private TableColumn<ToDoItem, String> priorityColumn;


    ObservableList<ToDoItem> list;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
        	list = FXCollections.observableArrayList();
            choiceList = FXCollections.observableArrayList();
            choiceList.addAll("Low", "Medium", "High");
            choiceBox.setItems(choiceList);
            choiceBox.setValue("Medium");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            statusColumn.setCellValueFactory(new PropertyValueFactory<ToDoItem, String>("status"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<ToDoItem, String>("description"));
            priorityColumn.setCellValueFactory(new PropertyValueFactory<ToDoItem, String>("priority"));
            tableView.setItems(list);
            String dataQuery = "Select Id, Description, Status, Priority FROM List";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(dataQuery);
            while (rs.next()) {
            	int id = rs.getInt("Id");
            	System.out.println(id);
                String description = rs.getString("Description");
                String status = rs.getString("Status");
                String priority = rs.getString("Priority");
                list.add(new ToDoItem(id, status, description, priority));
            }
            tableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

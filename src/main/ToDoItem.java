package main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ToDoItem {

    private SimpleStringProperty status, description, priority;
    private SimpleIntegerProperty id;
    
   
    
    public ToDoItem(String status, String description, String priority) {
		this.status = new SimpleStringProperty(status);
		this.description = new SimpleStringProperty(description);
		this.priority = new SimpleStringProperty(priority);
	}

	public ToDoItem(Integer id, String status, String description, String priority) {
    	this.id = new SimpleIntegerProperty(id);
        this.status = new SimpleStringProperty(status);
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleStringProperty(priority);
    }

    public Integer getId() {
		return id.get();
	}

	public void setId(SimpleIntegerProperty id) {
		this.id = id;
	}

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getPriority() {
        return priority.get();
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }
}

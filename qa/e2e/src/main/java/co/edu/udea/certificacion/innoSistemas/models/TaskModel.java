package co.edu.udea.certificacion.innoSistemas.models;

import io.cucumber.datatable.DataTable;
import java.util.Map;

public class TaskModel {

    private String name;
    private String description;
    private String status;
    private String priority;
    private String dueDate;

    public TaskModel(String name, String description, String status, String priority, String dueDate) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public static TaskModel fromDataTable(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        return new TaskModel(
                data.get("name"),
                data.get("description"),
                data.get("status"),
                data.get("priority"),
                data.get("due_date"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }
}

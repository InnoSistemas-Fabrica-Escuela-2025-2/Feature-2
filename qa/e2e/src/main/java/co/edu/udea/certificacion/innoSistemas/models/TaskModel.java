package co.edu.udea.certificacion.innoSistemas.models;

import io.cucumber.datatable.DataTable;
import java.util.Map;

public class TaskModel {

    private String name;
    private String description;
    private String project;
    private String responsible;
    private String dueDate;
    private String status; // Keeping status and priority as they might be used in verification or other
                           // scenarios
    private String priority;

    public TaskModel(String name, String description, String project, String responsible, String dueDate, String status,
            String priority) {
        this.name = name;
        this.description = description;
        this.project = project;
        this.responsible = responsible;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }

    public static TaskModel fromDataTable(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        return new TaskModel(
                data.get("name"),
                data.get("description"),
                data.get("project"),
                data.get("responsible"),
                data.get("due_date"),
                data.get("status"),
                data.get("priority"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProject() {
        return project;
    }

    public String getResponsible() {
        return responsible;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }
}

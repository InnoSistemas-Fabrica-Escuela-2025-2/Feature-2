package co.edu.udea.certificacion.innoSistemas.models;

import io.cucumber.datatable.DataTable;
import java.util.Map;

public class Project {

    private String name;
    private String description;
    private String startDate;
    private String endDate;

    public Project(String name, String description, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Project fromDataTable(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        return new Project(
                data.get("name"),
                data.get("description"),
                data.get("start_date"),
                data.get("end_date"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}

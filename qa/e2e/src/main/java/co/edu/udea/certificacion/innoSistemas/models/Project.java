package co.edu.udea.certificacion.innoSistemas.models;

import io.cucumber.datatable.DataTable;
import java.util.Map;

public class Project {

    private String name;
    private String description;
    private String objectives;
    private String deliveryDate;
    private String team;

    public Project(String name, String description, String objectives, String deliveryDate, String team) {
        this.name = name;
        this.description = description;
        this.objectives = objectives;
        this.deliveryDate = deliveryDate;
        this.team = team;
    }

    public static Project fromDataTable(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        return new Project(
                data.get("name"),
                data.get("description"),
                data.get("objectives"),
                data.get("delivery_date"),
                data.get("team"));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getObjectives() {
        return objectives;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getTeam() {
        return team;
    }
}

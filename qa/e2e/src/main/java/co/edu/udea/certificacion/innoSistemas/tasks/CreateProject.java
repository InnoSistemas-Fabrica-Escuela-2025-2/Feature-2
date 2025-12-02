package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.models.Project;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;

public class CreateProject implements Task {

    private final Project project;

    public CreateProject(Project project) {
        this.project = project;
    }

    public static CreateProject withDetails(Project project) {
        return new CreateProject(project);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                ClickOnElement.on(ProjectsPage.CREATE_PROJECT_BUTTON),
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, project.getName()),
                FillInputField.in(ProjectsPage.PROJECT_DESCRIPTION_INPUT, project.getDescription()),
                FillInputField.in(ProjectsPage.PROJECT_OBJECTIVES_INPUT, project.getObjectives()),
                FillInputField.in(ProjectsPage.DELIVERY_DATE_INPUT, project.getDeliveryDate()),
                SelectFromDropdown.option(project.getTeam(), ProjectsPage.TEAM_DROPDOWN),
                ClickOnElement.on(ProjectsPage.SUBMIT_PROJECT_BUTTON));
    }
}

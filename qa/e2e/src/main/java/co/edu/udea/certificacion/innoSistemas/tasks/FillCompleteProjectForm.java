package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.interactions.SetDateField;
import co.edu.udea.certificacion.innoSistemas.models.Project;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class FillCompleteProjectForm implements Task {

    private final Project project;

    private FillCompleteProjectForm(Project project) {
        this.project = project;
    }

    public static FillCompleteProjectForm with(Project project) {
        return new FillCompleteProjectForm(project);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, project.getName()),
                FillInputField.in(ProjectsPage.PROJECT_DESCRIPTION_INPUT, project.getDescription()),
                FillInputField.in(ProjectsPage.PROJECT_OBJECTIVES_INPUT, project.getObjectives())
        );
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(ProjectsPage.TEAM_DROPDOWN));
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(SelectFromDropdown.option(project.getTeam(), ProjectsPage.TEAM_DROPDOWN));
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(SetDateField.to(ProjectsPage.DELIVERY_DATE_INPUT, project.getDeliveryDate()));
        WaitTime.putWaitTimeOf(1200);
    }

}

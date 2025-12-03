package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.models.Project;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class FillPartialProjectForm implements Task {

    private final Project project;

    private FillPartialProjectForm(Project project) {
        this.project = project;
    }

    public static FillPartialProjectForm with(Project project) {
        return new FillPartialProjectForm(project);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(
                FillInputField.in(ProjectsPage.PROJECT_NAME_INPUT, project.getName()),
                FillInputField.in(ProjectsPage.PROJECT_OBJECTIVES_INPUT, project.getObjectives())
        );
        WaitTime.putWaitTimeOf(1200);
    }
}

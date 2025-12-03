package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Scroll;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;

public class NavigateToProject implements Task {

    private final String projectName;

    public NavigateToProject(String projectName) {
        this.projectName = projectName;
    }

    public static NavigateToProject named(String projectName) {
        return new NavigateToProject(projectName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Scroll.to(ProjectsPage.PROJECT_CARD.of(projectName)),
                ClickOnElement.on(ProjectsPage.PROJECT_CARD.of(projectName)));
    }
}

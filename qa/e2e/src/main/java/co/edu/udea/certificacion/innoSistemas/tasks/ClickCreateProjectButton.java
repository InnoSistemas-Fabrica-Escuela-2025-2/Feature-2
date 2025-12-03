package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.waits.WaitUntil;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class ClickCreateProjectButton implements Task {

    public static ClickCreateProjectButton now() {
        return new ClickCreateProjectButton();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                ClickOnElement.on(ProjectsPage.CREATE_PROJECT_BUTTON),
                WaitUntil.the(ProjectsPage.PROJECT_NAME_INPUT, isVisible()).forNoMoreThan(15).seconds()
        );
    }
}

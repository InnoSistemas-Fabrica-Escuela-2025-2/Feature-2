package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class SubmitProjectForm implements Task {

    public static SubmitProjectForm now() {
        return new SubmitProjectForm();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(ProjectsPage.SUBMIT_PROJECT_BUTTON));
        WaitTime.putWaitTimeOf(1200);
    }

}

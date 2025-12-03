package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class ConfirmCancelProject implements Task {

    public static ConfirmCancelProject now() {
        return new ConfirmCancelProject();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(ProjectsPage.CONFIRM_CANCEL_BUTTON));
        WaitTime.putWaitTimeOf(1200);
    }
}

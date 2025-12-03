package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;

public class ConfirmCancelProject implements Task {

    public static ConfirmCancelProject now() {
        return new ConfirmCancelProject();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(ClickOnElement.on(ProjectsPage.CONFIRM_CANCEL_BUTTON));
        sleep(5000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

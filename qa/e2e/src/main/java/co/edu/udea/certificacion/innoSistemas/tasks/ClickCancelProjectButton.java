package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.ProjectsPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class ClickCancelProjectButton implements Task {

    public static ClickCancelProjectButton now() {
        return new ClickCancelProjectButton();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(ProjectsPage.CANCEL_PROJECT_BUTTON));
        sleep(3000);
        WaitTime.putWaitTimeOf(1200);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class SubmitTaskForm implements Task {

    public static SubmitTaskForm now() {
        return new SubmitTaskForm();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(TasksPage.SUBMIT_TASK_BUTTON));
        WaitTime.putWaitTimeOf(1200);
    }

}

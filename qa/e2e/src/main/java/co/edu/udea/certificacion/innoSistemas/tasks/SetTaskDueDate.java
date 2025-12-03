package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.SetDateField;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;

public class SetTaskDueDate implements Task {

    private final String dueDate;

    private SetTaskDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public static SetTaskDueDate to(String dueDate) {
        return new SetTaskDueDate(dueDate);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(SetDateField.to(TasksPage.DUE_DATE_INPUT, dueDate));
        sleep(3000);
    }

    private void sleep(int millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}

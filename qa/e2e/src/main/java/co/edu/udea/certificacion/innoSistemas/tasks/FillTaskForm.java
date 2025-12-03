package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.models.TaskModel;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;
import co.edu.udea.certificacion.innoSistemas.utils.WaitTime;

public class FillTaskForm implements Task {

    private final TaskModel task;

    private FillTaskForm(TaskModel task) {
        this.task = task;
    }

    public static FillTaskForm with(TaskModel task) {
        return new FillTaskForm(task);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(
                FillInputField.in(TasksPage.TASK_TITLE_INPUT, task.getName()),
                FillInputField.in(TasksPage.TASK_DESCRIPTION_INPUT, task.getDescription())
        );
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(ClickOnElement.on(TasksPage.PROJECT_DROPDOWN));
        WaitTime.putWaitTimeOf(1200);
        actor.attemptsTo(SelectFromDropdown.option(task.getProject(), TasksPage.PROJECT_DROPDOWN));
        WaitTime.putWaitTimeOf(1200);
    }

}

package co.edu.udea.certificacion.innoSistemas.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import co.edu.udea.certificacion.innoSistemas.interactions.FillInputField;
import co.edu.udea.certificacion.innoSistemas.interactions.ClickOnElement;
import co.edu.udea.certificacion.innoSistemas.interactions.SelectFromDropdown;
import co.edu.udea.certificacion.innoSistemas.models.TaskModel;
import co.edu.udea.certificacion.innoSistemas.userinterfaces.TasksPage;

public class CreateTask implements Task {

    private final TaskModel task;

    public CreateTask(TaskModel task) {
        this.task = task;
    }

    public static CreateTask withDetails(TaskModel task) {
        return new CreateTask(task);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                ClickOnElement.on(TasksPage.ADD_TASK_BUTTON),
                FillInputField.in(TasksPage.TASK_NAME_INPUT, task.getName()),
                FillInputField.in(TasksPage.TASK_DESCRIPTION_INPUT, task.getDescription()),
                SelectFromDropdown.option(task.getProject(), TasksPage.PROJECT_DROPDOWN),
                SelectFromDropdown.option(task.getResponsible(), TasksPage.RESPONSIBLE_DROPDOWN),
                FillInputField.in(TasksPage.DUE_DATE_INPUT, task.getDueDate()),
                ClickOnElement.on(TasksPage.SAVE_TASK_BUTTON));
    }
}

package co.edu.udea.certificacion.innoSistemas.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class TasksPage {

    public static final Target ADD_TASK_BUTTON = Target.the("add task button")
            .located(By.cssSelector("button[aria-label='Add Task']"));

    public static final Target TASK_NAME_INPUT = Target.the("task name input")
            .located(By.name("taskName"));

    public static final Target TASK_DESCRIPTION_INPUT = Target.the("task description input")
            .located(By.name("taskDescription"));

    public static final Target TASK_STATUS_DROPDOWN = Target.the("task status dropdown")
            .located(By.name("status"));

    public static final Target TASK_PRIORITY_DROPDOWN = Target.the("task priority dropdown")
            .located(By.name("priority"));

    public static final Target DUE_DATE_INPUT = Target.the("due date input")
            .located(By.name("dueDate"));

    public static final Target SAVE_TASK_BUTTON = Target.the("save task button")
            .located(By.cssSelector("button[type='submit']"));

    public static final Target CONFIRMATION_MESSAGE = Target.the("confirmation message")
            .located(By.className("confirmation-message"));

    public static final Target TASK_ITEM = Target.the("task item with name {0}")
            .locatedBy("//div[contains(@class,'task-item')]//span[text()='{0}']");

    public static final Target TASK_STATUS_BADGE = Target.the("task status badge")
            .located(By.cssSelector(".task-status-badge"));

    public static final Target TASK_PRIORITY_BADGE = Target.the("task priority badge")
            .located(By.cssSelector(".task-priority-badge"));

    public static final Target TASK_MODAL = Target.the("task creation modal")
            .located(By.className("task-modal"));

    public static final Target VALIDATION_ERRORS = Target.the("validation errors")
            .located(By.className("validation-error"));
}
